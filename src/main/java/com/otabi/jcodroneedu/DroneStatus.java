package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.dronestatus.*;
import com.otabi.jcodroneedu.protocol.cardreader.CardColor;
import com.otabi.jcodroneedu.protocol.settings.Trim;

public class DroneStatus
{
    private Attitude attitude;
    private Altitude altitude;
    private Flow flow;
    private Motion motion;
    private Position position;
    private Range range;
    private RawFlow rawFlow;
    private RawMotion rawMotion;
    private State state;
    private CardColor cardColor;
    private Trim trim;

    public Attitude getAttitude()
    {
        return attitude;
    }

    public void setAttitude(Attitude attitude)
    {
        this.attitude = attitude;
    }

    public Altitude getAltitude()
    {
        return altitude;
    }

    public void setAltitude(Altitude altitude)
    {
        this.altitude = altitude;
    }

    public Flow getFlow()
    {
        return flow;
    }

    public void setFlow(Flow flow)
    {
        this.flow = flow;
    }

    public Motion getMotion()
    {
        return motion;
    }

    public void setMotion(Motion motion)
    {
        this.motion = motion;
    }

    public Position getPosition()
    {
        return position;
    }

    public void setPosition(Position position)
    {
        this.position = position;
    }

    public Range getRange()
    {
        return range;
    }

    public void setRange(Range range)
    {
        this.range = range;
    }

    public RawFlow getRawFlow()
    {
        return rawFlow;
    }

    public void setRawFlow(RawFlow rawFlow)
    {
        this.rawFlow = rawFlow;
    }

    public RawMotion getRawMotion()
    {
        return rawMotion;
    }

    public void setRawMotion(RawMotion rawMotion)
    {
        this.rawMotion = rawMotion;
    }

    public State getState()
    {
        return state;
    }

    public void setState(State state)
    {
        this.state = state;
    }

    public CardColor getCardColor()
    {
        return cardColor;
    }

    public void setCardColor(CardColor cardColor)
    {
        this.cardColor = cardColor;
    }

    public Trim getTrim()
    {
        return trim;
    }

    public void setTrim(Trim trim)
    {
        this.trim = trim;
    }

}
