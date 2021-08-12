/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

/**
 *
 * @author danrothman
 */
public class UIControlManager {
    public static void setSpinnerRange(Spinner s, int lowRange, int highRange,
            int initialValue) {
        s.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(lowRange,
                        highRange)
        );
        s.getValueFactory().setValue(initialValue);
    }
    
}
