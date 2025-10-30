package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.dronestatus.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.otabi.jcodroneedu.protocol.DataType;
import com.otabi.jcodroneedu.protocol.cardreader.CardColor;
import com.otabi.jcodroneedu.protocol.settings.Trim;
import com.otabi.jcodroneedu.protocol.controllerinput.Joystick;
import com.otabi.jcodroneedu.protocol.controllerinput.Button;

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
    private Joystick joystick;
    private Button button;

    private final Map<String, CompletableFuture<Object>> futures = new ConcurrentHashMap<>();

    public DroneStatus() {
        this.attitude = new Attitude();
        this.altitude = new Altitude();
        this.flow = new Flow();
        this.motion = new Motion();
        this.position = new Position();
        this.range = new Range();
        this.rawFlow = new RawFlow();
        this.rawMotion = new RawMotion();
        this.state = new State();
        this.cardColor = new CardColor();
        this.trim = new Trim();
        this.joystick = new Joystick();
        this.button = new Button();
    }

    public void waitForUpdate(DataType dataType)
    {
        CompletableFuture<Object> future = new CompletableFuture<>();
        futures.put(dataType.toString(), future);

        try {
            // Block and wait for the future to be completed
            future.get(50, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            future.completeExceptionally(e); // Cancel or handle timeout
            System.err.println("Warning: Timeout waiting for fresh data for tag: " + dataType.toString() + ". Returning possibly stale data.");
        } finally {
            futures.remove(dataType.toString()); // Clean up the map
        }
    }

    public void complete(DataType dataType)
    {
        CompletableFuture<Object> future = futures.remove(dataType.toString());
        if (future != null) {
            future.complete(null);
        }
    }

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

    public Joystick getJoystick()
    {
        return joystick;
    }

    public void setJoystick(Joystick joystick)
    {
        this.joystick = joystick;
    }

    public Button getButton()
    {
        return button;
    }

    public void setButton(Button button)
    {
        this.button = button;
    }
}
