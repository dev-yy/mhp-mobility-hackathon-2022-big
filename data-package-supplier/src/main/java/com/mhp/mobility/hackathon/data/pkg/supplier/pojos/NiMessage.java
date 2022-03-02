package com.mhp.mobility.hackathon.data.pkg.supplier.pojos;

public class NiMessage {
    private String nutzdatenIdentifier;

    public NiMessage() {
    }

    public NiMessage(String nutzdatenIdentifier) {
        this.nutzdatenIdentifier = nutzdatenIdentifier;
    }

    public String getNutzdatenIdentifier() {
        return nutzdatenIdentifier;
    }

    public void setNutzdatenIdentifier(String nutzdatenIdentifier) {
        this.nutzdatenIdentifier = nutzdatenIdentifier;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NiMessage{");
        sb.append("nutzdatenIdentifier='").append(nutzdatenIdentifier).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
