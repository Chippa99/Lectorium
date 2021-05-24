package ApiYoutube;

import Recorders.AbstractRecord;
import Sources.ScreenAreaSource;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BroadcastCreator {

    private static YouTube youtube;
    protected final static ExecutorService executor = Executors.newSingleThreadExecutor();
    public static void main(String[] args) {
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube");

        try {
            Credential credential = AuthYoutube.authorize(scopes, "createbroadcast");

            youtube = new YouTube.Builder(AuthYoutube.HTTP_TRANSPORT, AuthYoutube.JSON_FACTORY, credential)
                    .setApplicationName("youtube-cmdline-createbroadcast-sample").build();

            String title = "TEST1";
            System.out.println("You chose " + title + " for broadcast title.");

            LiveBroadcastSnippet broadcastSnippet = new LiveBroadcastSnippet();
            broadcastSnippet.setTitle(title);
            Instant now = Instant.now().plus(3, ChronoUnit.HOURS);
            broadcastSnippet.setScheduledStartTime(new DateTime(now.toEpochMilli()));
            broadcastSnippet.setScheduledEndTime(new DateTime(now.plus(2, ChronoUnit.DAYS).toEpochMilli()));

            LiveBroadcastStatus status = new LiveBroadcastStatus();
            status.setPrivacyStatus("public");

            LiveBroadcast broadcast = new LiveBroadcast();
            broadcast.setKind("youtube#liveBroadcast");
            broadcast.setSnippet(broadcastSnippet);
            broadcast.setStatus(status);

            YouTube.LiveBroadcasts.Insert liveBroadcastInsert =
                    youtube.liveBroadcasts().insert("snippet,status", broadcast);
            LiveBroadcast returnedBroadcast = liveBroadcastInsert.execute();

            System.out.println("\n================== Returned Broadcast ==================\n");
            System.out.println("  - Id: " + returnedBroadcast.getId());
            System.out.println("  - Title: " + returnedBroadcast.getSnippet().getTitle());
            System.out.println("  - Description: " + returnedBroadcast.getSnippet().getDescription());
            System.out.println("  - Published At: " + returnedBroadcast.getSnippet().getPublishedAt());
            System.out.println(
                    "  - Scheduled Start Time: " + returnedBroadcast.getSnippet().getScheduledStartTime());
            System.out.println(
                    "  - Scheduled End Time: " + returnedBroadcast.getSnippet().getScheduledEndTime());

            title = "TEST_STREAM1";
            System.out.println("You chose " + title + " for stream title.");

            LiveStreamSnippet streamSnippet = new LiveStreamSnippet();
            streamSnippet.setTitle(title);

            CdnSettings cdnSettings = new CdnSettings();
            cdnSettings.setResolution("1080p");
            cdnSettings.setIngestionType("rtmp");
            cdnSettings.setFrameRate("30fps");

            LiveStream stream = new LiveStream();
            stream.setKind("youtube#liveStream");
            stream.setSnippet(streamSnippet);
            stream.setCdn(cdnSettings);

            YouTube.LiveStreams.Insert liveStreamInsert =
                    youtube.liveStreams().insert("snippet,cdn", stream);
            LiveStream returnedStream = liveStreamInsert.execute();
            executor.execute(() -> {
                AbstractRecord encoder = new RTMPEncoder(
                        returnedStream.getCdn().getIngestionInfo().getIngestionAddress(),
                        returnedStream.getCdn().getIngestionInfo().getStreamName(),
                        new ScreenAreaSource()
                );
                encoder.start(Path.of(""));
            });

            //TODO here stream RTMP

            System.out.println("\n================== Returned Stream ==================\n");
            System.out.println("  - Id: " + returnedStream.getId());
            System.out.println("  - Title: " + returnedStream.getSnippet().getTitle());
            System.out.println("  - Description: " + returnedStream.getSnippet().getDescription());
            System.out.println("  - Published At: " + returnedStream.getSnippet().getPublishedAt());
            System.out.println("Start at: " + returnedBroadcast.getSnippet().getScheduledStartTime());
            System.out.println("Address : " + returnedStream.getCdn().getIngestionInfo().getIngestionAddress());
            System.out.println("Stream name : " + returnedStream.getCdn().getIngestionInfo().getStreamName());

            YouTube.LiveBroadcasts.Bind liveBroadcastBind =
                    youtube.liveBroadcasts().bind(returnedBroadcast.getId(), "id,contentDetails");
            liveBroadcastBind.setStreamId(returnedStream.getId());
            returnedBroadcast = liveBroadcastBind.execute();

            System.out.println("\n================== Returned Bound Broadcast ==================\n");
            System.out.println("  - Broadcast Id: " + returnedBroadcast.getId());
            System.out.println(
                    "  - Bound Stream Id: " + returnedBroadcast.getContentDetails().getBoundStreamId());

        } catch (GoogleJsonResponseException e) {
            System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
            e.printStackTrace();

        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable t) {
            System.err.println("Throwable: " + t.getMessage());
            t.printStackTrace();
        }
    }
}