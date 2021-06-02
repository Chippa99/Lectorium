package Sources;

import Utils.SystemInfo;

public interface Settings {
    SystemInfo systemInfo = new SystemInfo();
    String[] getSetupSettings();

    void refreshPath(String newSource);
}



