import static Utils.RecordUtils.toUTF8;

public enum SourceType {
    AUTO {
        @Override
        public String toString() { return toUTF8("Автозахват презентации"); }
    },
    FULL{
        @Override
        public String toString() {
            return toUTF8("Полный экран");
        }
    },
    AREA{
        @Override
        public String toString() {
            return toUTF8("Область экрана");
        }
    },
    CAPTURE_FRAME{
        @Override
        public String toString() { return toUTF8("Захват окна"); }
    },
    PRESENTATION{
        @Override
        public String toString() {
            return toUTF8("Режим презентации");
        }
    }
}
