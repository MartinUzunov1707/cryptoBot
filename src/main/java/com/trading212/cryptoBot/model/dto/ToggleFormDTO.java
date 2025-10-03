package com.trading212.cryptoBot.model.dto;

public class ToggleFormDTO {
    public Boolean checked;

    public ToggleFormDTO(Boolean checked) {
        this.checked = checked;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}
