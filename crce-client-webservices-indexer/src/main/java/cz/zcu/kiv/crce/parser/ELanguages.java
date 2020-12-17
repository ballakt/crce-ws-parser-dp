package cz.zcu.kiv.crce.parser;

public enum ELanguages {
    JAVA("java");
    // CPP("cpp"), etc.

    private String language;

    ELanguages(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }
}
