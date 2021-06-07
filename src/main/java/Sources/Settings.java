package Sources;

import Utils.SystemInfo;

public interface Settings {
    String SEPARATOR = "\\|";
    SystemInfo systemInfo = new SystemInfo();
    String[] getSetupSettings();
}



