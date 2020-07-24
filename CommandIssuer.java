/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4260.b21abf3a3 modeling language!*/


import java.util.*;
import java.lang.Thread;

/**
 * This program will accept a series of command line arguments and will
 * issue them on standard output. You can specify the amount of time to
 * elapse before each command by preceding it with a number of seconds followed by a colon
 * Example arguments:
 * m 6:c 5:r 8:q would would output m, and 6 seconds later s, 5 seconds later r and so on.
 * This is intended to be used to test time-sensitive systems.
 * Pipe standard output of this program to standard input of the system under test.
 */
// line 8 "timedcommands.ump"
public class CommandIssuer implements Runnable
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //CommandIssuer Attributes
  private Command nextCommand;
  private int nextDelay;

  //CommandIssuer State Machines
  public enum Sm { initializing, processing, doingACommand, done }
  private Sm sm;
  
  //enumeration type of messages accepted by CommandIssuer
  protected enum MessageType { processCommands_M, timeoutdoingACommandToprocessing_M }
  
  MessageQueue queue;
  Thread removal;

  //CommandIssuer Associations
  private List<Command> commands;

  //Helper Variables
  private TimedEventHandler timeoutdoingACommandToprocessingHandler;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public CommandIssuer()
  {
    nextCommand = null;
    nextDelay = 0;
    commands = new ArrayList<Command>();
    setSm(Sm.initializing);
    queue = new MessageQueue();
    removal=new Thread(this);
    //start the thread of CommandIssuer
    removal.start();
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setNextCommand(Command aNextCommand)
  {
    boolean wasSet = false;
    nextCommand = aNextCommand;
    wasSet = true;
    return wasSet;
  }

  public boolean setNextDelay(int aNextDelay)
  {
    boolean wasSet = false;
    nextDelay = aNextDelay;
    wasSet = true;
    return wasSet;
  }

  public Command getNextCommand()
  {
    return nextCommand;
  }

  public int getNextDelay()
  {
    return nextDelay;
  }

  public String getSmFullName()
  {
    String answer = sm.toString();
    return answer;
  }

  public Sm getSm()
  {
    return sm;
  }

  public boolean _processCommands()
  {
    boolean wasEventProcessed = false;
    
    Sm aSm = sm;
    switch (aSm)
    {
      case initializing:
        setSm(Sm.processing);
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
    
    Sm aSm = sm;
    switch (aSm)
    {
      case processing:
        if (!(hasCommands()))
        {
          setSm(Sm.done);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  private boolean __autotransition2__()
  {
    boolean wasEventProcessed = false;
    
    Sm aSm = sm;
    switch (aSm)
    {
      case processing:
        if (hasCommands())
        {
          setSm(Sm.doingACommand);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean _timeoutdoingACommandToprocessing()
  {
    boolean wasEventProcessed = false;
    
    Sm aSm = sm;
    switch (aSm)
    {
      case doingACommand:
        exitSm();
        setSm(Sm.processing);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  private void exitSm()
  {
    switch(sm)
    {
      case doingACommand:
        // line 31 "timedcommands.ump"
        System.out.println(nextCommand.getName());      
        removeCommand(nextCommand);
        stopTimeoutdoingACommandToprocessingHandler();
        break;
    }
  }

  private void setSm(Sm aSm)
  {
    sm = aSm;

    // entry actions and do activities
    switch(sm)
    {
      case processing:
        __autotransition1__();
        __autotransition2__();
        break;
      case doingACommand:
        // line 24 "timedcommands.ump"
        nextCommand = getCommand(0);
        nextDelay = nextCommand.getDelay();
        startTimeoutdoingACommandToprocessingHandler();
        break;
      case done:
        // line 38 "timedcommands.ump"
        System.exit(0);
        break;
    }
  }
  /* Code from template association_GetMany */
  public Command getCommand(int index)
  {
    Command aCommand = commands.get(index);
    return aCommand;
  }

  public List<Command> getCommands()
  {
    List<Command> newCommands = Collections.unmodifiableList(commands);
    return newCommands;
  }

  public int numberOfCommands()
  {
    int number = commands.size();
    return number;
  }

  public boolean hasCommands()
  {
    boolean has = commands.size() > 0;
    return has;
  }

  public int indexOfCommand(Command aCommand)
  {
    int index = commands.indexOf(aCommand);
    return index;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfCommands()
  {
    return 0;
  }
  /* Code from template association_AddManyToOptionalOne */
  public boolean addCommand(Command aCommand)
  {
    boolean wasAdded = false;
    if (commands.contains(aCommand)) { return false; }
    CommandIssuer existingCommandIssuer = aCommand.getCommandIssuer();
    if (existingCommandIssuer == null)
    {
      aCommand.setCommandIssuer(this);
    }
    else if (!this.equals(existingCommandIssuer))
    {
      existingCommandIssuer.removeCommand(aCommand);
      addCommand(aCommand);
    }
    else
    {
      commands.add(aCommand);
    }
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeCommand(Command aCommand)
  {
    boolean wasRemoved = false;
    if (commands.contains(aCommand))
    {
      commands.remove(aCommand);
      aCommand.setCommandIssuer(null);
      wasRemoved = true;
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addCommandAt(Command aCommand, int index)
  {  
    boolean wasAdded = false;
    if(addCommand(aCommand))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfCommands()) { index = numberOfCommands() - 1; }
      commands.remove(aCommand);
      commands.add(index, aCommand);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveCommandAt(Command aCommand, int index)
  {
    boolean wasAdded = false;
    if(commands.contains(aCommand))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfCommands()) { index = numberOfCommands() - 1; }
      commands.remove(aCommand);
      commands.add(index, aCommand);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addCommandAt(aCommand, index);
    }
    return wasAdded;
  }

  private void startTimeoutdoingACommandToprocessingHandler()
  {
    timeoutdoingACommandToprocessingHandler = new TimedEventHandler(this,"timeoutdoingACommandToprocessing",nextDelay);
  }

  private void stopTimeoutdoingACommandToprocessingHandler()
  {
    timeoutdoingACommandToprocessingHandler.stop();
  }

  public static class TimedEventHandler extends TimerTask  
  {
    private CommandIssuer controller;
    private String timeoutMethodName;
    private double howLongInSeconds;
    private Timer timer;
    
    public TimedEventHandler(CommandIssuer aController, String aTimeoutMethodName, double aHowLongInSeconds)
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
      if ("timeoutdoingACommandToprocessing".equals(timeoutMethodName))
      {
        boolean shouldRestart = !controller.timeoutdoingACommandToprocessing();
        if (shouldRestart)
        {
          controller.startTimeoutdoingACommandToprocessingHandler();
        }
        return;
      }
    }
  }

  public void delete()
  {
    while( !commands.isEmpty() )
    {
      commands.get(0).setCommandIssuer(null);
    }
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

  public void processCommands ()
  {
    queue.put(new Message(MessageType.processCommands_M, null));
  }

  public boolean timeoutdoingACommandToprocessing ()
  {
    boolean wasAdded = false;
    queue.put(new Message(MessageType.timeoutdoingACommandToprocessing_M, null));
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
        case processCommands_M:
          status = _processCommands();
          break;
        case timeoutdoingACommandToprocessing_M:
          status = _timeoutdoingACommandToprocessing();
          break; 
        default:
      }
      if(!status)
      {
        // Error message is written or  exception is raised
      }
    }
  }
  // line 43 "timedcommands.ump"
   public static  void main(String [] argv){
    Thread.currentThread().setUncaughtExceptionHandler(new UmpleExceptionHandler());
    Thread.setDefaultUncaughtExceptionHandler(new UmpleExceptionHandler());
    CommandIssuer tt = new CommandIssuer();

    for (String anArg: argv) {
      String[] parts = anArg.split(":");

      if(parts.length == 0) {
        // Just a colon. Ignore.
      }
      else if(parts.length == 1 && !anArg.endsWith(":")) {
        // no colon
        tt.addCommand(new Command(0,parts[0]));
      }
      else if(parts.length >= 1) {
        // colon present
        int theDelay = 0;
        String theCommand = "";
        if(parts.length > 1) theCommand = parts[1];
        
        try  {
          theDelay = Integer.parseInt(parts[0]);
        }
        catch (Exception e) {
          // ignore badly formed numbers and assume zero
        }
        tt.addCommand(new Command(theDelay, theCommand));
      }
    }
    tt.processCommands();
  }


  public String toString()
  {
    return super.toString() + "["+
            "nextDelay" + ":" + getNextDelay()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "nextCommand" + "=" + (getNextCommand() != null ? !getNextCommand().equals(this)  ? getNextCommand().toString().replaceAll("  ","    ") : "this" : "null");
  }
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
    public UmpleSourceData CommandIssuer_setSm(){ return new UmpleSourceData().setFileNames("timedcommands.ump","timedcommands.ump").setUmpleLines(23, 37).setJavaLines(210, 216).setLengths(2, 1);}
    public UmpleSourceData CommandIssuer___autotransition1__(){ return new UmpleSourceData().setFileNames("timedcommands.ump").setUmpleLines(17).setJavaLines(129).setLengths(1);}
    public UmpleSourceData CommandIssuer___autotransition2__(){ return new UmpleSourceData().setFileNames("timedcommands.ump").setUmpleLines(19).setJavaLines(151).setLengths(1);}
    public UmpleSourceData CommandIssuer_main(){ return new UmpleSourceData().setFileNames("timedcommands.ump").setUmpleLines(42).setJavaLines(466).setLengths(28);}
    public UmpleSourceData CommandIssuer_exitSm(){ return new UmpleSourceData().setFileNames("timedcommands.ump").setUmpleLines(30).setJavaLines(189).setLengths(2);}

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