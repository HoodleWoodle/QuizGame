package quiz.model;

import quiz.client.view.GameFrame;

import java.util.ResourceBundle;

/**
 * @author Alex, Eric, Quirin, Stefan
 * @version 29.04.2016
 */
public enum Category {

    /***/
    ENTERTAINMENT,
    /***/
    GAMING,
    /***/
    HISTORY,
    /***/
    SCIENCE,
    /***/
    TECHNOLOGY;

    private static ResourceBundle localization = GameFrame.getLocalization();

    public static Category fromString(String value) {
        if (value.equalsIgnoreCase(localization.getString("ENTERTAINMENT")))
            return ENTERTAINMENT;
        else if (value.equalsIgnoreCase(localization.getString("GAMING")))
            return GAMING;
        else if (value.equalsIgnoreCase(localization.getString("HISTORY")))
            return HISTORY;
        else if (value.equalsIgnoreCase(localization.getString("SCIENCE")))
            return SCIENCE;
        else if (value.equalsIgnoreCase(localization.getString("TECHNOLOGY")))
            return TECHNOLOGY;
        else
            return null;
    }

    @Override
    public String toString() {
        switch (this) {
            case ENTERTAINMENT:
                return localization.getString("ENTERTAINMENT");
            case GAMING:
                return localization.getString("GAMING");
            case HISTORY:
                return localization.getString("HISTORY");
            case SCIENCE:
                return localization.getString("SCIENCE");
            case TECHNOLOGY:
                return localization.getString("TECHNOLOGY");
            default:
                return null;
        }
    }
}