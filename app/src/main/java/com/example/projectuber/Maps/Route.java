package com.example.projectuber.Maps;

import java.util.List;

public class Route {

    private OverViewPolyline overview_polyline;

    private List<Legs> legs;

    public List<Legs> getLegs() {
        return legs;
    }

    public void setLegs(List<Legs> legs) {
        this.legs = legs;
    }

    public OverViewPolyline getOverViewPolyline() {
        return overview_polyline;
    }

    public void setOverViewPolyline(OverViewPolyline overview_polyline) {
        this.overview_polyline = overview_polyline;
    }
}

