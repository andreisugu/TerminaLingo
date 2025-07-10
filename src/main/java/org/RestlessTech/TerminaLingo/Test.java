package org.RestlessTech.TerminaLingo;

import java.util.List;

public class Test {
    private int tip;
    private String propozitie; // Used by tip 1, 2, 4
    private String raspunsCorect; // Used by tip 1, 2
    private List<String> leftWords; // Used by tip 3
    private List<String> rightWords; // Used by tip 3

    // Default constructor (REQUIRED for Gson)
    public Test() {
    }

    // Getters and Setters for ALL fields (REQUIRED for Gson)
    public int getTip() {
        return tip;
    }

    public void setTip(int tip) {
        this.tip = tip;
    }

    public String getPropozitie() {
        return propozitie;
    }

    public void setPropozitie(String propozitie) {
        this.propozitie = propozitie;
    }

    public String getRaspunsCorect() {
        return raspunsCorect;
    }

    public void setRaspunsCorect(String raspunsCorect) {
        this.raspunsCorect = raspunsCorect;
    }

    public List<String> getLeftWords() {
        return leftWords;
    }

    public void setLeftWords(List<String> leftWords) {
        this.leftWords = leftWords;
    }

    public List<String> getRightWords() {
        return rightWords;
    }

    public void setRightWords(List<String> rightWords) {
        this.rightWords = rightWords;
    }

    @Override
    public String toString() {
        // A more comprehensive toString for debugging or logging
        StringBuilder sb = new StringBuilder();
        sb.append("Test{tip=").append(tip);
        if (propozitie != null) sb.append(", propozitie='").append(propozitie).append('\'');
        if (raspunsCorect != null) sb.append(", raspunsCorect='").append(raspunsCorect).append('\'');
        if (leftWords != null) sb.append(", leftWords=").append(leftWords);
        if (rightWords != null) sb.append(", rightWords=").append(rightWords);
        sb.append('}');
        return sb.toString();
    }
}
