package com.gopetting.android.models;

/**
 * Created by Sumit on 9/2/2016.
 */
public class FilterCheckBox {

    String name = "";
    boolean checked = false;

    public String getName() {
        return name;
    }

    public FilterCheckBox setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isChecked() {
        return checked;
    }

    public FilterCheckBox setChecked(boolean checked) {
        this.checked = checked;
        return this;
    }

}
