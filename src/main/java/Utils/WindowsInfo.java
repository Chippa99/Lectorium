package Utils;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.StdCallLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.*;
import java.util.List;

import static com.sun.jna.platform.win32.WinUser.*;

public class WindowsInfo {
    private static final Logger log = LoggerFactory.getLogger(WindowsInfo.class);
    private final static User32 user32 = User32.INSTANCE;
    public static Map<String, String> findAllClassNames() {
        final Map<String, String> classNames = new HashMap<>();

        user32.EnumWindows(new WndEnumProc() {
            public boolean callback(int hWnd, int lParam) {
                if (user32.IsWindowVisible(hWnd)) {

                    char[] bufferClass = new char[1024];
                    user32.GetClassNameW(hWnd, bufferClass, bufferClass.length);
                    String className = Native.toString(bufferClass);

                    byte[] bufferTitle = new byte[1024];
                    user32.GetWindowTextA(hWnd, bufferTitle, bufferTitle.length);
                    String title = Native.toString(bufferTitle);
                    classNames.put(className, title);
                }
                return true;
            }
        }, null);
        return classNames;
    }

    public static String findFirstFrame() {
        final List<WindowInfo> inflList = new ArrayList<WindowInfo>();
        final List<Integer> order = new ArrayList<Integer>();
        int top = user32.GetTopWindow(0);
        while (top != 0) {
            order.add(top);
            top = user32.GetWindow(top, User32.GW_HWNDNEXT);
        }
        user32.EnumWindows(new WndEnumProc() {
            public boolean callback(int hWnd, int lParam) {
                RECT rect = new RECT();
                user32.GetWindowRect(hWnd, rect);
                Rectangle rec = rect.toRectangle();
                POINT pos = new POINT();
                user32.GetCursorPos(pos);

                if (pos.x > rec.x && pos.x <= rec.width
                        && pos.y > rec.y && pos.y <= rec.height
                        && user32.IsWindowVisible(hWnd)) {
                  //  if (r.left > -32000) {     // minimized
                        byte[] buffer = new byte[1024];
                        user32.GetWindowTextA(hWnd, buffer, buffer.length);
                        String title = Native.toString(buffer); //new String(buffer, StandardCharsets.UTF_16LE);
                        inflList.add(new WindowInfo(hWnd, rect, title));
                 //   }
                }
                return true;
            }
        }, null);
        Collections.sort(inflList, new Comparator<WindowInfo>() {
            public int compare(WindowInfo o1, WindowInfo o2) {
                return order.indexOf(o1.hwnd) - order.indexOf(o2.hwnd);
            }
        });
        log.info("The following windows were detected: [{}]", inflList);
        return inflList.get(0).title;
    }

    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.loadLibrary("user32", User32.class);

        int GW_HWNDNEXT = 2;

        boolean EnumWindows(WndEnumProc wndenumproc, Pointer arg);

        int GetWindowTextA(int hWnd, byte[] lpString, int nMaxCount);

        boolean GetCursorPos(WinDef.POINT p);

        boolean IsWindowVisible(int hWnd);

        boolean SetForegroundWindow(WinDef.HWND hWnd);

        int GetWindowRect(int hWnd, RECT r);

        int GetTopWindow(int hWnd);

        int GetWindow(int hWnd, int flag);

        boolean	FlashWindowEx(WinUser.FLASHWINFO pfwi);

        int GetClassNameW(int hWnd, char[] lpClassName, int nMaxCount);
    }

    public interface WndEnumProc extends StdCallLibrary.StdCallCallback {
        boolean callback(int hWnd, int lParam);
    }

    public static class WindowInfo {
        int hwnd;
        RECT rect;
        String title;

        public WindowInfo(int hwnd, RECT rect, String title) {
            this.hwnd = hwnd;
            this.rect = rect;
            this.title = title;
        }

        public String toString() {
            return String.format("(%d,%d)-(%d,%d) : \"%s\"",
                    rect.left, rect.top, rect.right, rect.bottom, title);
        }
    }
}