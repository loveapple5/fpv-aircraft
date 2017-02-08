package com.dji.FPVDemo;

/**
 * Created by LEX on 2016/10/12.
 */
//public class Switchlistener {
//
//    public interface myListener {
//        public void onstateChange(boolean b);
//    }
//
//    boolean Boo = false;
//
//    public Switchlistener(boolean b){
//        setmyListener(b);
//    }
//
//    private myListener listener = null;
//
//    public myListener getmyListener() {
//        return listener;
//    }
//
//    public void setmyListener(myListener listener) {
//        this.listener = listener;
//    }
//
//
//    public void DIYchanged(){
//
//        if(listener != null && Boo ==true){
//            listener.onstateChange(Boo);
//        }
//    }
//    // -----------------------------
//
////    private boolean switchOn = false;
////    public void doYourWork() {
////        // do things here
////        // at some point
////        switchOn = true;
////        // now notify if someone is interested.
////        if (mListener != null)
////            mListener.onStateChange(switchOn);
////    }
//}


public class Switchlistener
{
    public Switchlistener(boolean value)
    {
        setValue(value);
    }

    private boolean myValue;
    public boolean getValue() { return myValue; }
    public void setValue( boolean value )
    {
        if (value != myValue)
        {
            myValue = value;
            signalChanged();
        }
    }

    public interface VariableChangeListener
    {
        public void onVariableChanged(Object... variableThatHasChanged);
    }

    private VariableChangeListener variableChangeListener;
    public void setVariableChangeListener(VariableChangeListener variableChangeListener)
    {
        this.variableChangeListener = variableChangeListener;
    }

    private void signalChanged()
    {
        if (variableChangeListener != null)
            variableChangeListener.onVariableChanged(myValue);
    }
}