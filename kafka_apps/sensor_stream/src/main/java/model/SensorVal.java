package model;

/*
* Data holder for sensor values
*/

public class SensorVal {

    private String datetime;
    private float reading;
    
    public SensorVal(String source_timestamp, float source_reading) {
            this.datetime = source_timestamp;
            this.reading = source_reading;
    }


    @Override
    public String toString() {
        
        String stamp = this.datetime; + " " + this.reading;
        
        return stamp;
    }


    public void setDatetime(String stamp){
        this.datetime = stamp;
    }

    public void setReading(Float value){
        this.reading = value;
    }

    public String getDatetime() {
        return this.datetime;
    }

    public Float getReading(){
        return this.reading;
    }
    
}