/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4989.72e5f1e0f modeling language!*/


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.lang.Thread;

/**
 * This program is a simulation of an oven using state machines
 * Copyright (C) 2020 Youcef Ben Ali
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
// line 18 "Oven.ump"
public class Oven implements Runnable
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Oven Attributes
  private boolean radiatingPower;
  private boolean ovenOn;
  private boolean paused;
  private boolean closedDoor;
  private boolean beeping;
  private int microwavePower;
  private int secondsLeft;
  private int temperature;
  private int targetTemperature;
  private int maxTemp;
  private int numOperations;

  //Oven State Machines
  public enum Fan { idleFan, fanOff, fanOn }
  private Fan fan;
  public enum RadiatingHeatElement { idleRad, radOn, radPaused, radOff }
  private RadiatingHeatElement radiatingHeatElement;
  public enum Microwave { idleMic, micOn, micPaused, micOff }
  private Microwave microwave;
  public enum Defroster { idleFrost, frostOn, frostPaused, frostOff }
  private Defroster defroster;
  public enum TemperatureControl { idleTemp, temperatureActive }
  private TemperatureControl temperatureControl;
  public enum Door { idleDoor, doorClosed, doorOpen }
  private Door door;
  public enum Exhaust { closedExhaust, openExhaust }
  private Exhaust exhaust;
  public enum Timer2 { idleTimer, activeTimer, inactiveTimer }
  private Timer2 timer2;
  public enum TimingSm { idle, countingDown }
  private TimingSm timingSm;
  
  //enumeration type of messages accepted by Oven
  protected enum MessageType { turnOffFan_M, turnOnFan_M, turnOffRadiator_M, turnOnRadiator_M, timerEnded_M, stop_M, pause_M, resume_M, timeoutradOffToidleRad_M, turnOnMicrowave_M, turnOffMicrowave_M, timeoutmicOffToidleMic_M, turnOnDefroster_M, turnOffDefroster_M, timeoutfrostOffToidleFrost_M, tempOn_M, openDoor_M, closedDoor_M, closeDoor_M, openExhaust_M, closeExhaust_M, timeOn_M, timeOff_M, startTimer_M, timeoutcountingDownTocountingDown_M, timeoutcountingDownToidle_M }
  
  MessageQueue queue;
  Thread removal;

  //Oven Do Activity Threads
  Thread doActivityRadiatingHeatElementRadOnThread = null;
  Thread doActivityMicrowaveMicOnThread = null;
  Thread doActivityDefrosterFrostOnThread = null;
  Thread doActivityTemperatureControlTemperatureActiveThread = null;

  //Helper Variables
  private TimedEventHandler timeoutradOffToidleRadHandler;
  private TimedEventHandler timeoutmicOffToidleMicHandler;
  private TimedEventHandler timeoutfrostOffToidleFrostHandler;
  private TimedEventHandler timeoutcountingDownTocountingDownHandler;
  private TimedEventHandler timeoutcountingDownToidleHandler;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Oven()
  {
    radiatingPower = false;
    ovenOn = false;
    paused = false;
    closedDoor = true;
    beeping = false;
    microwavePower = 1;
    secondsLeft = 0;
    temperature = 0;
    targetTemperature = 0;
    maxTemp = 400;
    numOperations = 0;
    setFan(Fan.idleFan);
    setRadiatingHeatElement(RadiatingHeatElement.idleRad);
    setMicrowave(Microwave.idleMic);
    setDefroster(Defroster.idleFrost);
    setTemperatureControl(TemperatureControl.idleTemp);
    setDoor(Door.idleDoor);
    setExhaust(Exhaust.closedExhaust);
    setTimer2(Timer2.idleTimer);
    setTimingSm(TimingSm.idle);
    queue = new MessageQueue();
    removal=new Thread(this);
    //start the thread of Oven
    removal.start();
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setRadiatingPower(boolean aRadiatingPower)
  {
    boolean wasSet = false;
    radiatingPower = aRadiatingPower;
    wasSet = true;
    return wasSet;
  }

  public boolean setOvenOn(boolean aOvenOn)
  {
    boolean wasSet = false;
    ovenOn = aOvenOn;
    wasSet = true;
    return wasSet;
  }

  public boolean setPaused(boolean aPaused)
  {
    boolean wasSet = false;
    paused = aPaused;
    wasSet = true;
    return wasSet;
  }

  public boolean setClosedDoor(boolean aClosedDoor)
  {
    boolean wasSet = false;
    closedDoor = aClosedDoor;
    wasSet = true;
    return wasSet;
  }

  public boolean setBeeping(boolean aBeeping)
  {
    boolean wasSet = false;
    beeping = aBeeping;
    wasSet = true;
    return wasSet;
  }

  public boolean setMicrowavePower(int aMicrowavePower)
  {
    boolean wasSet = false;
    microwavePower = aMicrowavePower;
    wasSet = true;
    return wasSet;
  }

  public boolean setSecondsLeft(int aSecondsLeft)
  {
    boolean wasSet = false;
    secondsLeft = aSecondsLeft;
    wasSet = true;
    return wasSet;
  }

  public boolean setTemperature(int aTemperature)
  {
    boolean wasSet = false;
    temperature = aTemperature;
    wasSet = true;
    return wasSet;
  }

  public boolean setTargetTemperature(int aTargetTemperature)
  {
    boolean wasSet = false;
    targetTemperature = aTargetTemperature;
    wasSet = true;
    return wasSet;
  }

  public boolean setMaxTemp(int aMaxTemp)
  {
    boolean wasSet = false;
    maxTemp = aMaxTemp;
    wasSet = true;
    return wasSet;
  }

  public boolean setNumOperations(int aNumOperations)
  {
    boolean wasSet = false;
    numOperations = aNumOperations;
    wasSet = true;
    return wasSet;
  }

  /**
   * -- INSTANCE VARIABLES
   * If true power is high, else power is low. Low by default
   */
  public boolean getRadiatingPower()
  {
    return radiatingPower;
  }

  public boolean getOvenOn()
  {
    return ovenOn;
  }

  public boolean getPaused()
  {
    return paused;
  }

  public boolean getClosedDoor()
  {
    return closedDoor;
  }

  public boolean getBeeping()
  {
    return beeping;
  }

  /**
   * Microwave power from a scale of 1-10
   */
  public int getMicrowavePower()
  {
    return microwavePower;
  }

  public int getSecondsLeft()
  {
    return secondsLeft;
  }

  /**
   * Current temperature inside oven
   */
  public int getTemperature()
  {
    return temperature;
  }

  /**
   * Temperature requested by user
   */
  public int getTargetTemperature()
  {
    return targetTemperature;
  }

  /**
   * Max temperature for safety
   */
  public int getMaxTemp()
  {
    return maxTemp;
  }

  public int getNumOperations()
  {
    return numOperations;
  }

  public String getFanFullName()
  {
    String answer = fan.toString();
    return answer;
  }

  public String getRadiatingHeatElementFullName()
  {
    String answer = radiatingHeatElement.toString();
    return answer;
  }

  public String getMicrowaveFullName()
  {
    String answer = microwave.toString();
    return answer;
  }

  public String getDefrosterFullName()
  {
    String answer = defroster.toString();
    return answer;
  }

  public String getTemperatureControlFullName()
  {
    String answer = temperatureControl.toString();
    return answer;
  }

  public String getDoorFullName()
  {
    String answer = door.toString();
    return answer;
  }

  public String getExhaustFullName()
  {
    String answer = exhaust.toString();
    return answer;
  }

  public String getTimer2FullName()
  {
    String answer = timer2.toString();
    return answer;
  }

  public String getTimingSmFullName()
  {
    String answer = timingSm.toString();
    return answer;
  }

  public Fan getFan()
  {
    return fan;
  }

  public RadiatingHeatElement getRadiatingHeatElement()
  {
    return radiatingHeatElement;
  }

  public Microwave getMicrowave()
  {
    return microwave;
  }

  public Defroster getDefroster()
  {
    return defroster;
  }

  public TemperatureControl getTemperatureControl()
  {
    return temperatureControl;
  }

  public Door getDoor()
  {
    return door;
  }

  public Exhaust getExhaust()
  {
    return exhaust;
  }

  public Timer2 getTimer2()
  {
    return timer2;
  }

  public TimingSm getTimingSm()
  {
    return timingSm;
  }

  public boolean _turnOffFan()
  {
    boolean wasEventProcessed = false;
    
    Fan aFan = fan;
    switch (aFan)
    {
      case idleFan:
        setFan(Fan.fanOff);
        wasEventProcessed = true;
        break;
      case fanOn:
        setFan(Fan.fanOff);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _turnOnFan()
  {
    boolean wasEventProcessed = false;
    
    Fan aFan = fan;
    switch (aFan)
    {
      case idleFan:
        setFan(Fan.fanOn);
        wasEventProcessed = true;
        break;
      case fanOff:
        setFan(Fan.fanOn);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _turnOffRadiator()
  {
    boolean wasEventProcessed = false;
    
    RadiatingHeatElement aRadiatingHeatElement = radiatingHeatElement;
    switch (aRadiatingHeatElement)
    {
      case idleRad:
        setRadiatingHeatElement(RadiatingHeatElement.radOff);
        wasEventProcessed = true;
        break;
      case radOn:
        exitRadiatingHeatElement();
        setRadiatingHeatElement(RadiatingHeatElement.radOff);
        wasEventProcessed = true;
        break;
      case radPaused:
        // line 80 "Oven.ump"
        secondsLeft=0;
        setRadiatingHeatElement(RadiatingHeatElement.radOff);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _turnOnRadiator()
  {
    boolean wasEventProcessed = false;
    
    RadiatingHeatElement aRadiatingHeatElement = radiatingHeatElement;
    switch (aRadiatingHeatElement)
    {
      case idleRad:
        if (getSecondsLeft()>0&&getOvenOn()==false&&getClosedDoor())
        {
          setRadiatingHeatElement(RadiatingHeatElement.radOn);
          wasEventProcessed = true;
          break;
        }
        if (getSecondsLeft()==0)
        {
        // line 59 "Oven.ump"
          display("Time is set to zero, ensure time is above 0");
          setRadiatingHeatElement(RadiatingHeatElement.idleRad);
          wasEventProcessed = true;
          break;
        }
        if (getOvenOn()==true)
        {
        // line 60 "Oven.ump"
          display("The oven is currently being used, please cancel other cooking operations");
          setRadiatingHeatElement(RadiatingHeatElement.idleRad);
          wasEventProcessed = true;
          break;
        }
        if (!getClosedDoor())
        {
        // line 61 "Oven.ump"
          display("Oven door is open, ensure it is closed");
          setRadiatingHeatElement(RadiatingHeatElement.idleRad);
          wasEventProcessed = true;
          break;
        }
        break;
      case radOff:
        exitRadiatingHeatElement();
        setRadiatingHeatElement(RadiatingHeatElement.radOn);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _timerEnded()
  {
    boolean wasEventProcessed = false;
    
    RadiatingHeatElement aRadiatingHeatElement = radiatingHeatElement;
    Microwave aMicrowave = microwave;
    Defroster aDefroster = defroster;
    Timer2 aTimer2 = timer2;
    switch (aRadiatingHeatElement)
    {
      case radOn:
        exitRadiatingHeatElement();
        // line 71 "Oven.ump"
        nextOperation();
        setRadiatingHeatElement(RadiatingHeatElement.radOff);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    switch (aMicrowave)
    {
      case micOn:
        exitMicrowave();
        // line 103 "Oven.ump"
        nextOperation();
        setMicrowave(Microwave.micOff);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    switch (aDefroster)
    {
      case frostOn:
        exitDefroster();
        // line 131 "Oven.ump"
        nextOperation();
        setDefroster(Defroster.frostOff);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    switch (aTimer2)
    {
      case activeTimer:
        setTimer2(Timer2.inactiveTimer);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _stop()
  {
    boolean wasEventProcessed = false;
    
    RadiatingHeatElement aRadiatingHeatElement = radiatingHeatElement;
    Microwave aMicrowave = microwave;
    Defroster aDefroster = defroster;
    Timer2 aTimer2 = timer2;
    switch (aRadiatingHeatElement)
    {
      case radOn:
        exitRadiatingHeatElement();
        // line 72 "Oven.ump"
        secondsLeft=0;
        setRadiatingHeatElement(RadiatingHeatElement.radOff);
        wasEventProcessed = true;
        break;
      case radPaused:
        // line 81 "Oven.ump"
        secondsLeft=0;
        setRadiatingHeatElement(RadiatingHeatElement.radOff);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    switch (aMicrowave)
    {
      case micOn:
        exitMicrowave();
        // line 102 "Oven.ump"
        secondsLeft=0;
        setMicrowave(Microwave.micOff);
        wasEventProcessed = true;
        break;
      case micPaused:
        // line 110 "Oven.ump"
        secondsLeft=0;
        setMicrowave(Microwave.micOff);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    switch (aDefroster)
    {
      case frostOn:
        exitDefroster();
        // line 130 "Oven.ump"
        secondsLeft=0;
        setDefroster(Defroster.frostOff);
        wasEventProcessed = true;
        break;
      case frostPaused:
        // line 139 "Oven.ump"
        secondsLeft=0;
        setDefroster(Defroster.frostOff);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    switch (aTimer2)
    {
      case activeTimer:
        // line 195 "Oven.ump"
        secondsLeft=0;
        setTimer2(Timer2.inactiveTimer);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _pause()
  {
    boolean wasEventProcessed = false;
    
    RadiatingHeatElement aRadiatingHeatElement = radiatingHeatElement;
    Microwave aMicrowave = microwave;
    Defroster aDefroster = defroster;
    Exhaust aExhaust = exhaust;
    TimingSm aTimingSm = timingSm;
    switch (aRadiatingHeatElement)
    {
      case radOn:
        exitRadiatingHeatElement();
        setRadiatingHeatElement(RadiatingHeatElement.radPaused);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    switch (aMicrowave)
    {
      case micOn:
        exitMicrowave();
        setMicrowave(Microwave.micPaused);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    switch (aDefroster)
    {
      case frostOn:
        exitDefroster();
        setDefroster(Defroster.frostPaused);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    switch (aExhaust)
    {
      case openExhaust:
        setExhaust(Exhaust.closedExhaust);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    switch (aTimingSm)
    {
      case countingDown:
        exitTimingSm();
        setTimingSm(TimingSm.idle);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _resume()
  {
    boolean wasEventProcessed = false;
    
    RadiatingHeatElement aRadiatingHeatElement = radiatingHeatElement;
    Microwave aMicrowave = microwave;
    Defroster aDefroster = defroster;
    switch (aRadiatingHeatElement)
    {
      case radPaused:
        if (getClosedDoor())
        {
          setRadiatingHeatElement(RadiatingHeatElement.radOn);
          wasEventProcessed = true;
          break;
        }
        if (!getClosedDoor())
        {
        // line 79 "Oven.ump"
          display("Ensure the door is closed before resuming");
          setRadiatingHeatElement(RadiatingHeatElement.radPaused);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    switch (aMicrowave)
    {
      case micPaused:
        if (getClosedDoor())
        {
          setMicrowave(Microwave.micOn);
          wasEventProcessed = true;
          break;
        }
        if (!getClosedDoor())
        {
        // line 109 "Oven.ump"
          display("Ensure the door is closed before resuming");
          setMicrowave(Microwave.micPaused);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    switch (aDefroster)
    {
      case frostPaused:
        if (getClosedDoor())
        {
          setDefroster(Defroster.frostOn);
          wasEventProcessed = true;
          break;
        }
        if (!getClosedDoor())
        {
        // line 137 "Oven.ump"
          display("Ensure the door is closed before resuming");
          setDefroster(Defroster.frostPaused);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _timeoutradOffToidleRad()
  {
    boolean wasEventProcessed = false;
    
    RadiatingHeatElement aRadiatingHeatElement = radiatingHeatElement;
    switch (aRadiatingHeatElement)
    {
      case radOff:
        exitRadiatingHeatElement();
        setRadiatingHeatElement(RadiatingHeatElement.idleRad);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _turnOnMicrowave()
  {
    boolean wasEventProcessed = false;
    
    Microwave aMicrowave = microwave;
    switch (aMicrowave)
    {
      case idleMic:
        if (getSecondsLeft()>0&&getOvenOn()==false&&getClosedDoor())
        {
          setMicrowave(Microwave.micOn);
          wasEventProcessed = true;
          break;
        }
        if (getSecondsLeft()==0)
        {
        // line 94 "Oven.ump"
          display("Time is set to zero, ensure time is above 0");
          setMicrowave(Microwave.idleMic);
          wasEventProcessed = true;
          break;
        }
        if (getOvenOn()==true)
        {
        // line 95 "Oven.ump"
          display("The oven is currently being used, please cancel other cooking operations");
          setMicrowave(Microwave.idleMic);
          wasEventProcessed = true;
          break;
        }
        if (!getClosedDoor())
        {
        // line 96 "Oven.ump"
          display("Oven door is open, ensure it is closed");
          setMicrowave(Microwave.idleMic);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _turnOffMicrowave()
  {
    boolean wasEventProcessed = false;
    
    Microwave aMicrowave = microwave;
    switch (aMicrowave)
    {
      case micOn:
        exitMicrowave();
        // line 104 "Oven.ump"
        secondsLeft=0;
        setMicrowave(Microwave.micOff);
        wasEventProcessed = true;
        break;
      case micPaused:
        // line 111 "Oven.ump"
        secondsLeft=0;
        setMicrowave(Microwave.micOff);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _timeoutmicOffToidleMic()
  {
    boolean wasEventProcessed = false;
    
    Microwave aMicrowave = microwave;
    switch (aMicrowave)
    {
      case micOff:
        exitMicrowave();
        setMicrowave(Microwave.idleMic);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _turnOnDefroster()
  {
    boolean wasEventProcessed = false;
    
    Defroster aDefroster = defroster;
    switch (aDefroster)
    {
      case idleFrost:
        if (getSecondsLeft()>0&&getOvenOn()==false&&getClosedDoor())
        {
          setDefroster(Defroster.frostOn);
          wasEventProcessed = true;
          break;
        }
        if (getSecondsLeft()==0)
        {
        // line 122 "Oven.ump"
          display("Time is set to zero, ensure time is above 0");
          setDefroster(Defroster.idleFrost);
          wasEventProcessed = true;
          break;
        }
        if (getOvenOn()==true)
        {
        // line 123 "Oven.ump"
          display("The oven is currently being used, please cancel other cooking operations");
          setDefroster(Defroster.idleFrost);
          wasEventProcessed = true;
          break;
        }
        if (!getClosedDoor())
        {
        // line 124 "Oven.ump"
          display("Oven door is open, ensure it is closed");
          setDefroster(Defroster.idleFrost);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _turnOffDefroster()
  {
    boolean wasEventProcessed = false;
    
    Defroster aDefroster = defroster;
    switch (aDefroster)
    {
      case frostOn:
        exitDefroster();
        // line 132 "Oven.ump"
        secondsLeft=0;
        setDefroster(Defroster.frostOff);
        wasEventProcessed = true;
        break;
      case frostPaused:
        // line 138 "Oven.ump"
        secondsLeft=0;
        setDefroster(Defroster.frostOff);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _timeoutfrostOffToidleFrost()
  {
    boolean wasEventProcessed = false;
    
    Defroster aDefroster = defroster;
    switch (aDefroster)
    {
      case frostOff:
        exitDefroster();
        setDefroster(Defroster.idleFrost);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _tempOn(int heat)
  {
    boolean wasEventProcessed = false;
    
    TemperatureControl aTemperatureControl = temperatureControl;
    switch (aTemperatureControl)
    {
      case idleTemp:
        if (heat<getMaxTemp())
        {
        // line 149 "Oven.ump"
          targetTemperature = heat;
          setTemperatureControl(TemperatureControl.temperatureActive);
          wasEventProcessed = true;
          break;
        }
        if (heat>=getMaxTemp())
        {
        // line 150 "Oven.ump"
          display("Given temperature above maximum temperature");
          setTemperatureControl(TemperatureControl.idleTemp);
          wasEventProcessed = true;
          break;
        }
        break;
      case temperatureActive:
        if (heat<getMaxTemp())
        {
          exitTemperatureControl();
        // line 154 "Oven.ump"
          targetTemperature = heat;
          setTemperatureControl(TemperatureControl.temperatureActive);
          wasEventProcessed = true;
          break;
        }
        if (heat>=getMaxTemp())
        {
          exitTemperatureControl();
        // line 155 "Oven.ump"
          display("Given temperature above maximum temperature");
          setTemperatureControl(TemperatureControl.idleTemp);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _openDoor()
  {
    boolean wasEventProcessed = false;
    
    Door aDoor = door;
    switch (aDoor)
    {
      case idleDoor:
        setDoor(Door.doorOpen);
        wasEventProcessed = true;
        break;
      case doorClosed:
        setDoor(Door.doorOpen);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _closedDoor()
  {
    boolean wasEventProcessed = false;
    
    Door aDoor = door;
    switch (aDoor)
    {
      case idleDoor:
        setDoor(Door.doorClosed);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _closeDoor()
  {
    boolean wasEventProcessed = false;
    
    Door aDoor = door;
    switch (aDoor)
    {
      case doorOpen:
        setDoor(Door.doorClosed);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _openExhaust()
  {
    boolean wasEventProcessed = false;
    
    Exhaust aExhaust = exhaust;
    switch (aExhaust)
    {
      case closedExhaust:
        setExhaust(Exhaust.openExhaust);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _closeExhaust()
  {
    boolean wasEventProcessed = false;
    
    Exhaust aExhaust = exhaust;
    switch (aExhaust)
    {
      case openExhaust:
        setExhaust(Exhaust.closedExhaust);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _timeOn(int seconds)
  {
    boolean wasEventProcessed = false;
    
    Timer2 aTimer2 = timer2;
    switch (aTimer2)
    {
      case idleTimer:
        if (getOvenOn()==false)
        {
        // line 189 "Oven.ump"
          secondsLeft = seconds;
          setTimer2(Timer2.activeTimer);
          wasEventProcessed = true;
          break;
        }
        if (getOvenOn()==true)
        {
        // line 190 "Oven.ump"
          display("Timer currently being used for oven features");
          setTimer2(Timer2.idleTimer);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _timeOff()
  {
    boolean wasEventProcessed = false;
    
    Timer2 aTimer2 = timer2;
    switch (aTimer2)
    {
      case idleTimer:
        setTimer2(Timer2.inactiveTimer);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  private boolean __autotransition1__()
  {
    boolean wasEventProcessed = false;
    
    Timer2 aTimer2 = timer2;
    switch (aTimer2)
    {
      case inactiveTimer:
        setTimer2(Timer2.idleTimer);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _startTimer(int seconds)
  {
    boolean wasEventProcessed = false;
    
    TimingSm aTimingSm = timingSm;
    switch (aTimingSm)
    {
      case idle:
        setTimingSm(TimingSm.countingDown);
        wasEventProcessed = true;
        break;
      case countingDown:
        exitTimingSm();
        setTimingSm(TimingSm.countingDown);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _timeoutcountingDownTocountingDown()
  {
    boolean wasEventProcessed = false;
    
    TimingSm aTimingSm = timingSm;
    switch (aTimingSm)
    {
      case countingDown:
        if (getSecondsLeft()>=1&&!getPaused())
        {
          exitTimingSm();
        // line 210 "Oven.ump"
          displayTime();
          secondsLeft--;
          setTimingSm(TimingSm.countingDown);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _timeoutcountingDownToidle()
  {
    boolean wasEventProcessed = false;
    
    TimingSm aTimingSm = timingSm;
    switch (aTimingSm)
    {
      case countingDown:
        if (getSecondsLeft()<=0)
        {
          exitTimingSm();
          setTimingSm(TimingSm.idle);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  private void setFan(Fan aFan)
  {
    fan = aFan;

    // entry actions and do activities
    switch(fan)
    {
      case fanOff:
        // line 45 "Oven.ump"
        display("Fan has been turned off");
        break;
      case fanOn:
        // line 50 "Oven.ump"
        display("Fan has been turned on");
        break;
    }
  }

  private void exitRadiatingHeatElement()
  {
    switch(radiatingHeatElement)
    {
      case radOn:
        if (doActivityRadiatingHeatElementRadOnThread != null) { doActivityRadiatingHeatElementRadOnThread.interrupt(); }
        break;
      case radOff:
        stopTimeoutradOffToidleRadHandler();
        break;
    }
  }

  private void setRadiatingHeatElement(RadiatingHeatElement aRadiatingHeatElement)
  {
    radiatingHeatElement = aRadiatingHeatElement;

    // entry actions and do activities
    switch(radiatingHeatElement)
    {
      case radOn:
        // line 65 "Oven.ump"
        if(radiatingPower) display("Radiator is on at high power");
            else display("Radiator is on at low power");
        // line 69 "Oven.ump"
        turnOnFan();startTimer(secondsLeft);
        doActivityRadiatingHeatElementRadOnThread = new DoActivityThread(this,"doActivityRadiatingHeatElementRadOn");
        break;
      case radPaused:
        // line 77 "Oven.ump"
        display("Radiator is paused");turnOffFan();
        break;
      case radOff:
        // line 84 "Oven.ump"
        display("Radiator has been turned off");
        // line 85 "Oven.ump"
        turnOffFan(); beep(3); ovenOn=false;
        startTimeoutradOffToidleRadHandler();
        break;
    }
  }

  private void exitMicrowave()
  {
    switch(microwave)
    {
      case micOn:
        if (doActivityMicrowaveMicOnThread != null) { doActivityMicrowaveMicOnThread.interrupt(); }
        break;
      case micOff:
        stopTimeoutmicOffToidleMicHandler();
        break;
    }
  }

  private void setMicrowave(Microwave aMicrowave)
  {
    microwave = aMicrowave;

    // entry actions and do activities
    switch(microwave)
    {
      case micOn:
        // line 99 "Oven.ump"
        display("Microwave is on at " + microwavePower); startTimer(secondsLeft); openExhaust();
        doActivityMicrowaveMicOnThread = new DoActivityThread(this,"doActivityMicrowaveMicOn");
        break;
      case micPaused:
        // line 107 "Oven.ump"
        display("Microwave is paused");
        break;
      case micOff:
        // line 114 "Oven.ump"
        display("Microwave has been turned off"); closeExhaust(); beep(3); ovenOn = false;
        startTimeoutmicOffToidleMicHandler();
        break;
    }
  }

  private void exitDefroster()
  {
    switch(defroster)
    {
      case frostOn:
        if (doActivityDefrosterFrostOnThread != null) { doActivityDefrosterFrostOnThread.interrupt(); }
        break;
      case frostOff:
        stopTimeoutfrostOffToidleFrostHandler();
        break;
    }
  }

  private void setDefroster(Defroster aDefroster)
  {
    defroster = aDefroster;

    // entry actions and do activities
    switch(defroster)
    {
      case frostOn:
        // line 127 "Oven.ump"
        display("Defroster is now on"); startTimer(secondsLeft);
        doActivityDefrosterFrostOnThread = new DoActivityThread(this,"doActivityDefrosterFrostOn");
        break;
      case frostPaused:
        // line 135 "Oven.ump"
        display("Defroster is paused");
        break;
      case frostOff:
        // line 142 "Oven.ump"
        display("Defroster has been turned off");beep(3); ovenOn = false;
        startTimeoutfrostOffToidleFrostHandler();
        break;
    }
  }

  private void exitTemperatureControl()
  {
    switch(temperatureControl)
    {
      case temperatureActive:
        if (doActivityTemperatureControlTemperatureActiveThread != null) { doActivityTemperatureControlTemperatureActiveThread.interrupt(); }
        break;
    }
  }

  private void setTemperatureControl(TemperatureControl aTemperatureControl)
  {
    temperatureControl = aTemperatureControl;

    // entry actions and do activities
    switch(temperatureControl)
    {
      case temperatureActive:
        doActivityTemperatureControlTemperatureActiveThread = new DoActivityThread(this,"doActivityTemperatureControlTemperatureActive");
        break;
    }
  }

  private void setDoor(Door aDoor)
  {
    door = aDoor;

    // entry actions and do activities
    switch(door)
    {
      case doorClosed:
        // line 166 "Oven.ump"
        closedDoor = true; display("Door has been closed");
        break;
      case doorOpen:
        // line 170 "Oven.ump"
        closedDoor = false; display("Door has been opened"); pause();
        break;
    }
  }

  private void setExhaust(Exhaust aExhaust)
  {
    exhaust = aExhaust;

    // entry actions and do activities
    switch(exhaust)
    {
      case closedExhaust:
        // line 177 "Oven.ump"
        display("Exhaust vent is closed");
        break;
      case openExhaust:
        // line 181 "Oven.ump"
        display("Exhaust vent has been opened");
        break;
    }
  }

  private void setTimer2(Timer2 aTimer2)
  {
    timer2 = aTimer2;

    // entry actions and do activities
    switch(timer2)
    {
      case activeTimer:
        // line 194 "Oven.ump"
        startTimer(secondsLeft);
        break;
      case inactiveTimer:
        // line 199 "Oven.ump"
        beep(4);
        __autotransition1__();
        break;
    }
  }

  private void exitTimingSm()
  {
    switch(timingSm)
    {
      case countingDown:
        // line 219 "Oven.ump"
        if (secondsLeft <= 0) timerEnded();
        stopTimeoutcountingDownTocountingDownHandler();
        stopTimeoutcountingDownToidleHandler();
        break;
    }
  }

  private void setTimingSm(TimingSm aTimingSm)
  {
    timingSm = aTimingSm;

    // entry actions and do activities
    switch(timingSm)
    {
      case countingDown:
        startTimeoutcountingDownTocountingDownHandler();
        startTimeoutcountingDownToidleHandler();
        break;
    }
  }

  private void doActivityRadiatingHeatElementRadOn()
  {
    try
    {
      // line 70 "Oven.ump"
      toggleOven();
      Thread.sleep(1);
    }
    catch (InterruptedException e)
    {

    }
  }

  private void doActivityMicrowaveMicOn()
  {
    try
    {
      // line 100 "Oven.ump"
      toggleOven();
      Thread.sleep(1);
    }
    catch (InterruptedException e)
    {

    }
  }

  private void doActivityDefrosterFrostOn()
  {
    try
    {
      // line 128 "Oven.ump"
      toggleOven();
      Thread.sleep(1);
    }
    catch (InterruptedException e)
    {

    }
  }

  private void doActivityTemperatureControlTemperatureActive()
  {
    try
    {
      // line 156 "Oven.ump"
      checkForTemp();
      Thread.sleep(1);
    }
    catch (InterruptedException e)
    {

    }
  }

  private static class DoActivityThread extends Thread
  {
    Oven controller;
    String doActivityMethodName;
    
    public DoActivityThread(Oven aController,String aDoActivityMethodName)
    {
      controller = aController;
      doActivityMethodName = aDoActivityMethodName;
      start();
    }
    
    public void run()
    {
      if ("doActivityRadiatingHeatElementRadOn".equals(doActivityMethodName))
      {
        controller.doActivityRadiatingHeatElementRadOn();
      }
        else if ("doActivityMicrowaveMicOn".equals(doActivityMethodName))
      {
        controller.doActivityMicrowaveMicOn();
      }
        else if ("doActivityDefrosterFrostOn".equals(doActivityMethodName))
      {
        controller.doActivityDefrosterFrostOn();
      }
        else if ("doActivityTemperatureControlTemperatureActive".equals(doActivityMethodName))
      {
        controller.doActivityTemperatureControlTemperatureActive();
      }
    }
  }

  private void startTimeoutradOffToidleRadHandler()
  {
    timeoutradOffToidleRadHandler = new TimedEventHandler(this,"timeoutradOffToidleRad",1);
  }

  private void stopTimeoutradOffToidleRadHandler()
  {
    timeoutradOffToidleRadHandler.stop();
  }

  private void startTimeoutmicOffToidleMicHandler()
  {
    timeoutmicOffToidleMicHandler = new TimedEventHandler(this,"timeoutmicOffToidleMic",1);
  }

  private void stopTimeoutmicOffToidleMicHandler()
  {
    timeoutmicOffToidleMicHandler.stop();
  }

  private void startTimeoutfrostOffToidleFrostHandler()
  {
    timeoutfrostOffToidleFrostHandler = new TimedEventHandler(this,"timeoutfrostOffToidleFrost",1);
  }

  private void stopTimeoutfrostOffToidleFrostHandler()
  {
    timeoutfrostOffToidleFrostHandler.stop();
  }

  private void startTimeoutcountingDownTocountingDownHandler()
  {
    timeoutcountingDownTocountingDownHandler = new TimedEventHandler(this,"timeoutcountingDownTocountingDown",1.0);
  }

  private void stopTimeoutcountingDownTocountingDownHandler()
  {
    timeoutcountingDownTocountingDownHandler.stop();
  }

  private void startTimeoutcountingDownToidleHandler()
  {
    timeoutcountingDownToidleHandler = new TimedEventHandler(this,"timeoutcountingDownToidle",1.0);
  }

  private void stopTimeoutcountingDownToidleHandler()
  {
    timeoutcountingDownToidleHandler.stop();
  }

  public static class TimedEventHandler extends TimerTask  
  {
    private Oven controller;
    private String timeoutMethodName;
    private double howLongInSeconds;
    private Timer timer;
    
    public TimedEventHandler(Oven aController, String aTimeoutMethodName, double aHowLongInSeconds)
    {
      controller = aController;
      timeoutMethodName = aTimeoutMethodName;
      howLongInSeconds = aHowLongInSeconds;
      timer = new Timer();
      timer.schedule(this, (long)howLongInSeconds*1000);
    }
    
    public void stop()
    {
      timer.cancel();
    }
    
    public void run ()
    {
      if ("timeoutradOffToidleRad".equals(timeoutMethodName))
      {
        boolean shouldRestart = !controller.timeoutradOffToidleRad();
        if (shouldRestart)
        {
          controller.startTimeoutradOffToidleRadHandler();
        }
        return;
      }
      if ("timeoutmicOffToidleMic".equals(timeoutMethodName))
      {
        boolean shouldRestart = !controller.timeoutmicOffToidleMic();
        if (shouldRestart)
        {
          controller.startTimeoutmicOffToidleMicHandler();
        }
        return;
      }
      if ("timeoutfrostOffToidleFrost".equals(timeoutMethodName))
      {
        boolean shouldRestart = !controller.timeoutfrostOffToidleFrost();
        if (shouldRestart)
        {
          controller.startTimeoutfrostOffToidleFrostHandler();
        }
        return;
      }
      if ("timeoutcountingDownTocountingDown".equals(timeoutMethodName))
      {
        boolean shouldRestart = !controller.timeoutcountingDownTocountingDown();
        if (shouldRestart)
        {
          controller.startTimeoutcountingDownTocountingDownHandler();
        }
        return;
      }
      if ("timeoutcountingDownToidle".equals(timeoutMethodName))
      {
        boolean shouldRestart = !controller.timeoutcountingDownToidle();
        if (shouldRestart)
        {
          controller.startTimeoutcountingDownToidleHandler();
        }
        return;
      }
    }
  }

  public void delete()
  {
    removal.interrupt();
  }

  protected class Message
  {
    MessageType type;
    
    //Message parameters
    Vector<Object> param;
    
    public Message(MessageType t, Vector<Object> p)
    {
      type = t; 
      param = p;
    }

    @Override
    public String toString()
    {
      return type + "," + param;
    }
  }
  
  protected class MessageQueue {
    Queue<Message> messages = new LinkedList<Message>();
    
    public synchronized void put(Message m)
    {
      messages.add(m); 
      notify();
    }

    public synchronized Message getNext()
    {
      try {
        while (messages.isEmpty()) 
        {
          wait();
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return null;
      }

      //The element to be removed
      Message m = messages.remove(); 
      return (m);
    }
  }

  //------------------------------
  //messages accepted 
  //------------------------------

  public void turnOffFan ()
  {
    queue.put(new Message(MessageType.turnOffFan_M, null));
  }

  public void turnOnFan ()
  {
    queue.put(new Message(MessageType.turnOnFan_M, null));
  }

  public void turnOffRadiator ()
  {
    queue.put(new Message(MessageType.turnOffRadiator_M, null));
  }

  public void turnOnRadiator ()
  {
    queue.put(new Message(MessageType.turnOnRadiator_M, null));
  }

  public void timerEnded ()
  {
    queue.put(new Message(MessageType.timerEnded_M, null));
  }

  public void stop ()
  {
    queue.put(new Message(MessageType.stop_M, null));
  }

  public void pause ()
  {
    queue.put(new Message(MessageType.pause_M, null));
  }

  public void resume ()
  {
    queue.put(new Message(MessageType.resume_M, null));
  }

  public boolean timeoutradOffToidleRad ()
  {
    boolean wasAdded = false;
    queue.put(new Message(MessageType.timeoutradOffToidleRad_M, null));
    wasAdded = true;
    return wasAdded;
  }

  public void turnOnMicrowave ()
  {
    queue.put(new Message(MessageType.turnOnMicrowave_M, null));
  }

  public void turnOffMicrowave ()
  {
    queue.put(new Message(MessageType.turnOffMicrowave_M, null));
  }

  public boolean timeoutmicOffToidleMic ()
  {
    boolean wasAdded = false;
    queue.put(new Message(MessageType.timeoutmicOffToidleMic_M, null));
    wasAdded = true;
    return wasAdded;
  }

  public void turnOnDefroster ()
  {
    queue.put(new Message(MessageType.turnOnDefroster_M, null));
  }

  public void turnOffDefroster ()
  {
    queue.put(new Message(MessageType.turnOffDefroster_M, null));
  }

  public boolean timeoutfrostOffToidleFrost ()
  {
    boolean wasAdded = false;
    queue.put(new Message(MessageType.timeoutfrostOffToidleFrost_M, null));
    wasAdded = true;
    return wasAdded;
  }

  public void tempOn (int heat)
  {
    Vector v = new Vector(1);
    v.add(0, heat);
    queue.put(new Message(MessageType.tempOn_M, v));
  }

  public void openDoor ()
  {
    queue.put(new Message(MessageType.openDoor_M, null));
  }

  public void closedDoor ()
  {
    queue.put(new Message(MessageType.closedDoor_M, null));
  }

  public void closeDoor ()
  {
    queue.put(new Message(MessageType.closeDoor_M, null));
  }

  public void openExhaust ()
  {
    queue.put(new Message(MessageType.openExhaust_M, null));
  }

  public void closeExhaust ()
  {
    queue.put(new Message(MessageType.closeExhaust_M, null));
  }

  public void timeOn (int seconds)
  {
    Vector v = new Vector(1);
    v.add(0, seconds);
    queue.put(new Message(MessageType.timeOn_M, v));
  }

  public void timeOff ()
  {
    queue.put(new Message(MessageType.timeOff_M, null));
  }

  public void startTimer (int seconds)
  {
    Vector v = new Vector(1);
    v.add(0, seconds);
    queue.put(new Message(MessageType.startTimer_M, v));
  }

  public boolean timeoutcountingDownTocountingDown ()
  {
    boolean wasAdded = false;
    queue.put(new Message(MessageType.timeoutcountingDownTocountingDown_M, null));
    wasAdded = true;
    return wasAdded;
  }

  public boolean timeoutcountingDownToidle ()
  {
    boolean wasAdded = false;
    queue.put(new Message(MessageType.timeoutcountingDownToidle_M, null));
    wasAdded = true;
    return wasAdded;
  }

  
  @Override
  public void run ()
  {
    boolean status=false;
    while (true) 
    {
      Message m = queue.getNext();
      if(m == null)  return;
      
      switch (m.type)
      {
        case turnOffFan_M:
          status = _turnOffFan();
          break;
        case turnOnFan_M:
          status = _turnOnFan();
          break;
        case turnOffRadiator_M:
          status = _turnOffRadiator();
          break;
        case turnOnRadiator_M:
          status = _turnOnRadiator();
          break;
        case timerEnded_M:
          status = _timerEnded();
          break;
        case stop_M:
          status = _stop();
          break;
        case pause_M:
          status = _pause();
          break;
        case resume_M:
          status = _resume();
          break;
        case timeoutradOffToidleRad_M:
          status = _timeoutradOffToidleRad();
          break;
        case turnOnMicrowave_M:
          status = _turnOnMicrowave();
          break;
        case turnOffMicrowave_M:
          status = _turnOffMicrowave();
          break;
        case timeoutmicOffToidleMic_M:
          status = _timeoutmicOffToidleMic();
          break;
        case turnOnDefroster_M:
          status = _turnOnDefroster();
          break;
        case turnOffDefroster_M:
          status = _turnOffDefroster();
          break;
        case timeoutfrostOffToidleFrost_M:
          status = _timeoutfrostOffToidleFrost();
          break;
        case tempOn_M:
          status = _tempOn((int) m.param.elementAt(0));
          break;
        case openDoor_M:
          status = _openDoor();
          break;
        case closedDoor_M:
          status = _closedDoor();
          break;
        case closeDoor_M:
          status = _closeDoor();
          break;
        case openExhaust_M:
          status = _openExhaust();
          break;
        case closeExhaust_M:
          status = _closeExhaust();
          break;
        case timeOn_M:
          status = _timeOn((int) m.param.elementAt(0));
          break;
        case timeOff_M:
          status = _timeOff();
          break;
        case startTimer_M:
          status = _startTimer((int) m.param.elementAt(0));
          break;
        case timeoutcountingDownTocountingDown_M:
          status = _timeoutcountingDownTocountingDown();
          break;
        case timeoutcountingDownToidle_M:
          status = _timeoutcountingDownToidle();
          break; 
        default:
      }
      if(!status)
      {
        // Error message is written or  exception is raised
      }
    }
  }

  /**
   * --INSTANCE METHODS
   */
  // line 225 "Oven.ump"
  public void checkForTemp(){
    //Monitors temperature to stop opreations if temperature exceeds target/critical temperature
      while(true){
        //System.out.println("Here");
        if (temperature >= maxTemp || temperature>=targetTemperature){
          display("Max temperature exceeded, stopping all cooking");
          stop();
          break;
        }
      }
  }

  // line 236 "Oven.ump"
  public void changeTemp(int heat){
    //For testing purposes not a function that would be controlled by user
      temperature=heat;
      if (temperature >= maxTemp || temperature>=targetTemperature){
          display("Max temperature exceeded, stopping all cooking");
          stop();
      }
  }

  // line 245 "Oven.ump"
  public void toggleOven(){
    try{
          Thread.sleep(100);
          ovenOn = true;
        }
        catch(InterruptedException ex){
          Thread.currentThread().interrupt();
        }
  }

  // line 254 "Oven.ump"
  public void beep(int repition){
    //Method to simulate beeping sounds
      if(!beeping){
        beeping = false;
        for (int i = 0; i<repition; i++){
          display("SOUND: BEEP (1 second)");
          try{
            Thread.sleep(1000);
          }
          catch(InterruptedException ex){
            Thread.currentThread().interrupt();
          }
        }
        beeping = true;
      }
  }

  // line 271 "Oven.ump"
  public void setTime(int seconds){
    secondsLeft = seconds;
      display("Time has been set to "+ seconds);
  }

  // line 276 "Oven.ump"
  public void setMicroWavePower(int power){
    microwavePower = power;
      display("Power has been set to "+ power);
  }

  // line 280 "Oven.ump"
  public void toggleradiatingPower(){
    //Switches between high and low power for radiator
      radiatingPower = !radiatingPower;
      if (radiatingPower){
        display("Power has been set to high");
      }
      else{
        display("Power has been set to low");
      }
  }

  // line 291 "Oven.ump"
  public void clearQueue(){
    for(int i = 0; i<operations.length; i++){
        operations[i] = null;
      }
      numOperations=0;
      display("Queue/Operation has been completed or stopped");
  }

  // line 298 "Oven.ump"
  public void shift(){
    //Deletes first element and shifts all elements in the operations array to the left by one thereby progressing through the queue
      if (numOperations>0){
        operations[0] = null;
        for (int i = 1; i<operations.length;i++){
          operations[i-1] = operations[i];
        }
        numOperations--;
      }

      if (numOperations==0){

      }
  }

  // line 312 "Oven.ump"
  public void nextOperation(){
    //Reads the first element of the queue and executes the operation
      if (numOperations>0){
        Object[] operation = operations[0];
        //System.out.println("Current operation" + operation[0]);
        String command = operation[0].toString();
        int time=0;
        if (operation[1]!= null && operation[1] instanceof Integer){
          time = (Integer) operation[1];
        }
        try{
            Thread.sleep(500);
          }
          catch(InterruptedException ex){
            Thread.currentThread().interrupt();
        }
        switch(command){
          case "microwave":
            setTime(time);
            turnOnMicrowave();
            break;
          case "radiator":
            setTime(time);
            turnOnRadiator();
            break;
          case "defrost":
            setTime(time);
            turnOnDefroster();
            break;
        }
        shift();
      }
      else{
        clearQueue();
        beep(5);
      }
  }

  // line 349 "Oven.ump"
  public void queue(String order, int time){
    //Adds new operations to the queue when called
      if (numOperations<20){
        Object[] operation = new Object[]{order,time};
        operations[numOperations] = operation;
        numOperations++;
      }
      else{
        display("Exceeding max operations");
      }
  }


  /**
   * --Display functions
   */
  // line 363 "Oven.ump"
  public void displayTime(){
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss ");  
      Date date = new Date();  
      formatter.format(date);
      System.out.println(formatter.format(date) + "DISPLAY: "+secondsLeft);
  }

  // line 370 "Oven.ump"
  public void display(String s){
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss ");  
      Date date = new Date();  
      formatter.format(date);
      System.out.println(formatter.format(date)+ s);
  }

  // line 377 "Oven.ump"
  public void displayCommands(){
    String s = "Available commands:\nmicrowaveOn\nmicrowaveOff\nradiatorOn\nradiatorOff\ndefrosterOn\ndefrosterOff\ntime\npowerMicrowave\npowerRadiator\nopendoor\nclosedoor\nresume\nstop\ntimer\ntemperature\ninternaltemp(for testing only)\nboth(microwave and radiator)\nqueue\ncommands";
      display(s);
  }

  // line 382 "Oven.ump"
  public void displaySecondaryCommands(){
    String s = "Queue commands:\nmicrowave\nradiator\ndefrost\ndone\ncancel\ncommands";
      display(s);
  }


  /**
   * -- MAIN FUNCTION
   */
  // line 391 "Oven.ump"
   public static  void main(String [] args){
    Thread.currentThread().setUncaughtExceptionHandler(new UmpleExceptionHandler());
    Thread.setDefaultUncaughtExceptionHandler(new UmpleExceptionHandler());
    System.out.println("Oven.ump  Copyright (C) 2020  Youcef Ben Ali\nThis program comes with ABSOLUTELY NO WARRANTY.\nThis is free software, and you are welcome to redistribute it\nunder certain conditions");


        Oven test = new Oven();
        Scanner s = new Scanner(System.in);
        int length;
        String command;
        test.displayCommands();
        while(true) {
        command = s.nextLine();
        switch(command) {
          case "q": System.exit(0);
          case "queue":
            test.display("Note: Max number of operations is 20");
            if (test.ovenOn == false){
                test.displaySecondaryCommands();
                list: while(true){
                  command = s.nextLine();
                  switch(command){
                    case "q": System.exit(0);
                    case "microwave":
                      test.display("Enter time");
                      command = s.nextLine();
                      length = Integer.parseInt(command);
                      test.queue("microwave", length);
                      break;
                    case "radiator":
                      test.display("Enter time");
                      command = s.nextLine();
                      length = Integer.parseInt(command);
                      test.queue("radiator", length);
                      break;
                    case "defrost":
                      test.display("Enter time");
                      command = s.nextLine();
                      length = Integer.parseInt(command);
                      test.queue("defrost", length);
                      break;
                    case "done":
                      test.nextOperation();
                      break list;
                    case "cancel":
                      test.clearQueue();
                      break list;
                    case "commands":
                      test.displaySecondaryCommands();
                      break;
                    }
                }
              }
            else{
              test.display("Oven in use, please stop oven operations before creating a queue");
            }
            
            break;
          case "microwaveOn":
            test.turnOnMicrowave();
            break;
          case "microwaveOff":
            test.turnOffMicrowave();
            break;
          case "radiatorOn":
            test.turnOnRadiator();
            break;
          case "radiatorOff":
            test.turnOffRadiator();
            break;
          case "defrosterOn":
            test.turnOnDefroster();
            break;
          case "defrosterOff":
            test.turnOffDefroster();
            break;
          case "time":
            test.display("Enter time");
            command = s.nextLine();
            length = Integer.parseInt(command);
            test.setTime(length);
            break;
          case "powerMicrowave":
            test.display("Enter power (1-10):");
            command = s.nextLine();
            length = Integer.parseInt(command);
            test.setMicroWavePower(length);
            break;
          case "powerRadiator":
            test.toggleradiatingPower();
            break;
          case "opendoor":
            test.openDoor();
            break;
          case "closedoor":
            test.closeDoor();
            break;
          case "pause":
            test.pause();
            break;
          case "resume":
            test.resume();
            break;
          case "stop":
            test.stop();
            break;
          case "timer":
            test.display("Enter time:");
            command = s.nextLine();
            length = Integer.parseInt(command);
            test.timeOn(length);
            break;
          case "temperature":
            test.display("Enter temperature:");
            command = s.nextLine();
            length = Integer.parseInt(command);
            test.tempOn(length);
            break;
          case "internalTemp":
            test.display("Enter temperature:");
            command = s.nextLine();
            length = Integer.parseInt(command);
            test.changeTemp(length);
            break;
          case "both":
            test.turnOnRadiator();
            test.turnOnMicrowave();
            break;
          case "commands":
            test.displayCommands();
            break;
          default:
            test.display("Not a valid command/button");
        }
      }
  }


  public String toString()
  {
    return super.toString() + "["+
            "radiatingPower" + ":" + getRadiatingPower()+ "," +
            "ovenOn" + ":" + getOvenOn()+ "," +
            "paused" + ":" + getPaused()+ "," +
            "closedDoor" + ":" + getClosedDoor()+ "," +
            "beeping" + ":" + getBeeping()+ "," +
            "microwavePower" + ":" + getMicrowavePower()+ "," +
            "secondsLeft" + ":" + getSecondsLeft()+ "," +
            "temperature" + ":" + getTemperature()+ "," +
            "targetTemperature" + ":" + getTargetTemperature()+ "," +
            "maxTemp" + ":" + getMaxTemp()+ "," +
            "numOperations" + ":" + getNumOperations()+ "]";
  }  
  //------------------------
  // DEVELOPER CODE - PROVIDED AS-IS
  //------------------------
  
  // line 31 "Oven.ump"
  Object[][] operations = new Object[20][] ;

  
  public static class UmpleExceptionHandler implements Thread.UncaughtExceptionHandler
  {
    public void uncaughtException(Thread t, Throwable e)
    {
      translate(e);
      if(e.getCause()!=null)
      {
        translate(e.getCause());
      }
      e.printStackTrace();
    }
    public void translate(Throwable e)
    {
      java.util.List<StackTraceElement> result = new java.util.ArrayList<StackTraceElement>();
      StackTraceElement[] elements = e.getStackTrace();
      try
      {
        for(StackTraceElement element:elements)
        {
          String className = element.getClassName();
          String methodName = element.getMethodName();
          boolean methodFound = false;
          int index = className.lastIndexOf('.')+1;
          try {
            java.lang.reflect.Method query = this.getClass().getMethod(className.substring(index)+"_"+methodName,new Class[]{});
            UmpleSourceData sourceInformation = (UmpleSourceData)query.invoke(this,new Object[]{});
            for(int i=0;i<sourceInformation.size();++i)
            {
              // To compensate for any offsets caused by injected code we need to loop through the other references to this function
              //  and adjust the start / length of the function.
              int functionStart = sourceInformation.getJavaLine(i) + (("main".equals(methodName))?3:1);
              int functionEnd = functionStart + sourceInformation.getLength(i);
              int afterInjectionLines = 0;
              //  We can leverage the fact that all inject statements are added to the uncaught exception list 
              //   before the functions that they are within
              for (int j = 0; j < i; j++) {
                if (sourceInformation.getJavaLine(j) - 1 >= functionStart &&
                    sourceInformation.getJavaLine(j) - 1 <= functionEnd &&
                    sourceInformation.getJavaLine(j) - 1 <= element.getLineNumber()) {
                    // A before injection, +2 for the comments surrounding the injected code
                    if (sourceInformation.getJavaLine(j) - 1 == functionStart) {
                        functionStart += sourceInformation.getLength(j) + 2;
                        functionEnd += sourceInformation.getLength(j) + 2;
                    } else {
                        // An after injection
                        afterInjectionLines += sourceInformation.getLength(j) + 2;
                        functionEnd += sourceInformation.getLength(j) + 2;
                    }
                }
              }
              int distanceFromStart = element.getLineNumber() - functionStart - afterInjectionLines;
              if(distanceFromStart>=0&&distanceFromStart<=sourceInformation.getLength(i))
              {
                result.add(new StackTraceElement(element.getClassName(),element.getMethodName(),sourceInformation.getFileName(i),sourceInformation.getUmpleLine(i)+distanceFromStart));
                methodFound = true;
                break;
              }
            }
          }
          catch (Exception e2){}
          if(!methodFound)
          {
            result.add(element);
          }
        }
      }
      catch (Exception e1)
      {
        e1.printStackTrace();
      }
      e.setStackTrace(result.toArray(new StackTraceElement[0]));
    }
  //The following methods Map Java lines back to their original Umple file / line    
    public UmpleSourceData Oven_beep(){ return new UmpleSourceData().setFileNames("Oven.ump").setUmpleLines(253).setJavaLines(2068).setLengths(14);}
    public UmpleSourceData Oven_turnOffDefroster(){ return new UmpleSourceData().setFileNames("Oven.ump","Oven.ump").setUmpleLines(131, 137).setJavaLines(953, 959).setLengths(1, 1);}
    public UmpleSourceData Oven_displayTime(){ return new UmpleSourceData().setFileNames("Oven.ump").setUmpleLines(362).setJavaLines(2191).setLengths(4);}
    public UmpleSourceData Oven_turnOnDefroster(){ return new UmpleSourceData().setFileNames("Oven.ump","Oven.ump","Oven.ump","Oven.ump","Oven.ump","Oven.ump","Oven.ump").setUmpleLines(120, 121, 121, 122, 122, 123, 123).setJavaLines(907, 914, 916, 923, 925, 932, 934).setLengths(1, 1, 1, 1, 1, 1, 1);}
    public UmpleSourceData Oven_shift(){ return new UmpleSourceData().setFileNames("Oven.ump").setUmpleLines(297).setJavaLines(2119).setLengths(12);}
    public UmpleSourceData Oven_changeTemp(){ return new UmpleSourceData().setFileNames("Oven.ump").setUmpleLines(235).setJavaLines(2047).setLengths(6);}
    public UmpleSourceData Oven_clearQueue(){ return new UmpleSourceData().setFileNames("Oven.ump").setUmpleLines(290).setJavaLines(2110).setLengths(5);}
    public UmpleSourceData Oven_main(){ return new UmpleSourceData().setFileNames("Oven.ump").setUmpleLines(390).setJavaLines(2223).setLengths(132);}
    public UmpleSourceData Oven_toggleradiatingPower(){ return new UmpleSourceData().setFileNames("Oven.ump").setUmpleLines(279).setJavaLines(2098).setLengths(8);}
    public UmpleSourceData Oven_turnOnMicrowave(){ return new UmpleSourceData().setFileNames("Oven.ump","Oven.ump","Oven.ump","Oven.ump","Oven.ump","Oven.ump","Oven.ump").setUmpleLines(92, 93, 93, 94, 94, 95, 95).setJavaLines(815, 822, 824, 831, 833, 840, 842).setLengths(1, 1, 1, 1, 1, 1, 1);}
    public UmpleSourceData Oven_displaySecondaryCommands(){ return new UmpleSourceData().setFileNames("Oven.ump").setUmpleLines(381).setJavaLines(2213).setLengths(2);}
    public UmpleSourceData Oven_turnOffMicrowave(){ return new UmpleSourceData().setFileNames("Oven.ump","Oven.ump").setUmpleLines(103, 110).setJavaLines(861, 867).setLengths(1, 1);}
    public UmpleSourceData Oven_setFan(){ return new UmpleSourceData().setFileNames("Oven.ump","Oven.ump").setUmpleLines(44, 49).setJavaLines(1285, 1289).setLengths(1, 1);}
    public UmpleSourceData Oven_turnOffRadiator(){ return new UmpleSourceData().setFileNames("Oven.ump").setUmpleLines(79).setJavaLines(442).setLengths(1);}
    public UmpleSourceData Oven_timeoutcountingDownTocountingDown(){ return new UmpleSourceData().setFileNames("Oven.ump","Oven.ump").setUmpleLines(209, 209).setJavaLines(1236, 1239).setLengths(1, 2);}
    public UmpleSourceData Oven_setMicrowave(){ return new UmpleSourceData().setFileNames("Oven.ump","Oven.ump","Oven.ump").setUmpleLines(98, 106, 113).setJavaLines(1358, 1363, 1367).setLengths(1, 1, 1);}
    public UmpleSourceData Oven_setTime(){ return new UmpleSourceData().setFileNames("Oven.ump").setUmpleLines(270).setJavaLines(2086).setLengths(2);}
    public UmpleSourceData Oven_nextOperation(){ return new UmpleSourceData().setFileNames("Oven.ump").setUmpleLines(311).setJavaLines(2135).setLengths(35);}
    public UmpleSourceData Oven_resume(){ return new UmpleSourceData().setFileNames("Oven.ump","Oven.ump","Oven.ump","Oven.ump","Oven.ump","Oven.ump","Oven.ump","Oven.ump","Oven.ump").setUmpleLines(77, 78, 78, 107, 108, 108, 135, 136, 136).setJavaLines(720, 727, 729, 744, 751, 753, 768, 775, 777).setLengths(1, 1, 1, 1, 1, 1, 1, 1, 1);}
    public UmpleSourceData Oven_timeOn(){ return new UmpleSourceData().setFileNames("Oven.ump","Oven.ump","Oven.ump","Oven.ump").setUmpleLines(188, 188, 189, 189).setJavaLines(1145, 1147, 1154, 1156).setLengths(1, 1, 1, 1);}
    public UmpleSourceData Oven_timerEnded(){ return new UmpleSourceData().setFileNames("Oven.ump","Oven.ump","Oven.ump").setUmpleLines(70, 102, 130).setJavaLines(514, 527, 540).setLengths(1, 1, 1);}
    public UmpleSourceData Oven_checkForTemp(){ return new UmpleSourceData().setFileNames("Oven.ump").setUmpleLines(224).setJavaLines(2034).setLengths(9);}
    public UmpleSourceData Oven_doActivityMicrowaveMicOn(){ return new UmpleSourceData().setFileNames("Oven.ump").setUmpleLines(99).setJavaLines(1532).setLengths(1);}
    public UmpleSourceData Oven_setRadiatingHeatElement(){ return new UmpleSourceData().setFileNames("Oven.ump","Oven.ump","Oven.ump","Oven.ump","Oven.ump").setUmpleLines(64, 68, 76, 83, 84).setJavaLines(1316, 1318, 1323, 1327, 1328).setLengths(2, 1, 1, 1, 1);}
    public UmpleSourceData Oven_tempOn(){ return new UmpleSourceData().setFileNames("Oven.ump","Oven.ump","Oven.ump","Oven.ump","Oven.ump","Oven.ump","Oven.ump","Oven.ump").setUmpleLines(148, 148, 149, 149, 153, 153, 154, 154).setJavaLines(999, 1001, 1008, 1010, 1019, 1022, 1029, 1032).setLengths(1, 1, 1, 1, 1, 1, 1, 1);}
    public UmpleSourceData Oven_setDefroster(){ return new UmpleSourceData().setFileNames("Oven.ump","Oven.ump","Oven.ump").setUmpleLines(126, 134, 141).setJavaLines(1395, 1400, 1404).setLengths(1, 1, 1);}
    public UmpleSourceData Oven_doActivityRadiatingHeatElementRadOn(){ return new UmpleSourceData().setFileNames("Oven.ump").setUmpleLines(69).setJavaLines(1518).setLengths(1);}
    public UmpleSourceData Oven_display(){ return new UmpleSourceData().setFileNames("Oven.ump").setUmpleLines(369).setJavaLines(2199).setLengths(4);}
    public UmpleSourceData Oven_setExhaust(){ return new UmpleSourceData().setFileNames("Oven.ump","Oven.ump").setUmpleLines(176, 180).setJavaLines(1460, 1464).setLengths(1, 1);}
    public UmpleSourceData Oven_toggleOven(){ return new UmpleSourceData().setFileNames("Oven.ump").setUmpleLines(244).setJavaLines(2057).setLengths(7);}
    public UmpleSourceData Oven_turnOnRadiator(){ return new UmpleSourceData().setFileNames("Oven.ump","Oven.ump","Oven.ump","Oven.ump","Oven.ump","Oven.ump","Oven.ump").setUmpleLines(57, 58, 58, 59, 59, 60, 60).setJavaLines(463, 470, 472, 479, 481, 488, 490).setLengths(1, 1, 1, 1, 1, 1, 1);}
    public UmpleSourceData Oven_doActivityDefrosterFrostOn(){ return new UmpleSourceData().setFileNames("Oven.ump").setUmpleLines(127).setJavaLines(1546).setLengths(1);}
    public UmpleSourceData Oven_timeoutcountingDownToidle(){ return new UmpleSourceData().setFileNames("Oven.ump").setUmpleLines(214).setJavaLines(1262).setLengths(1);}
    public UmpleSourceData Oven_setMicroWavePower(){ return new UmpleSourceData().setFileNames("Oven.ump").setUmpleLines(275).setJavaLines(2092).setLengths(2);}
    public UmpleSourceData Oven_displayCommands(){ return new UmpleSourceData().setFileNames("Oven.ump").setUmpleLines(376).setJavaLines(2207).setLengths(2);}
    public UmpleSourceData Oven_stop(){ return new UmpleSourceData().setFileNames("Oven.ump","Oven.ump","Oven.ump","Oven.ump","Oven.ump","Oven.ump","Oven.ump").setUmpleLines(71, 80, 101, 109, 129, 138, 194).setJavaLines(574, 580, 593, 599, 612, 618, 630).setLengths(1, 1, 1, 1, 1, 1, 1);}
    public UmpleSourceData Oven_setTimer2(){ return new UmpleSourceData().setFileNames("Oven.ump","Oven.ump").setUmpleLines(193, 198).setJavaLines(1478, 1482).setLengths(1, 1);}
    public UmpleSourceData Oven_doActivityTemperatureControlTemperatureActive(){ return new UmpleSourceData().setFileNames("Oven.ump").setUmpleLines(155).setJavaLines(1560).setLengths(1);}
    public UmpleSourceData Oven_setDoor(){ return new UmpleSourceData().setFileNames("Oven.ump","Oven.ump").setUmpleLines(165, 169).setJavaLines(1442, 1446).setLengths(1, 1);}
    public UmpleSourceData Oven_exitTimingSm(){ return new UmpleSourceData().setFileNames("Oven.ump").setUmpleLines(218).setJavaLines(1494).setLengths(1);}
    public UmpleSourceData Oven_queue(){ return new UmpleSourceData().setFileNames("Oven.ump").setUmpleLines(348).setJavaLines(2174).setLengths(9);}

  }
  public static class UmpleSourceData
  {
    String[] umpleFileNames;
    Integer[] umpleLines;
    Integer[] umpleJavaLines;
    Integer[] umpleLengths;
    
    public UmpleSourceData(){
    }
    public String getFileName(int i){
      return umpleFileNames[i];
    }
    public Integer getUmpleLine(int i){
      return umpleLines[i];
    }
    public Integer getJavaLine(int i){
      return umpleJavaLines[i];
    }
    public Integer getLength(int i){
      return umpleLengths[i];
    }
    public UmpleSourceData setFileNames(String... filenames){
      umpleFileNames = filenames;
      return this;
    }
    public UmpleSourceData setUmpleLines(Integer... umplelines){
      umpleLines = umplelines;
      return this;
    }
    public UmpleSourceData setJavaLines(Integer... javalines){
      umpleJavaLines = javalines;
      return this;
    }
    public UmpleSourceData setLengths(Integer... lengths){
      umpleLengths = lengths;
      return this;
    }
    public int size(){
      return umpleFileNames.length;
    }
  }
}