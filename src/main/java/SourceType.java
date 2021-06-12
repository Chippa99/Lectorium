public enum SourceType {
    AUTO {
        @Override
        public String toString() { return "Автопоиск презентации"; }
    },
    FULL{
        @Override
        public String toString() {
            return "Полный экран";
        }
    },
    AREA{
        @Override
        public String toString() {
            return "Область экрана";
        }
    },
    CAPTURE_FRAME{
        @Override
        public String toString() { return "Захват окна"; }
    },
    PRESENTATION{
        @Override
        public String toString() {
            return "Режим презентации";
        }
    }
}
