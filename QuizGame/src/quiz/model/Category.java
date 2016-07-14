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