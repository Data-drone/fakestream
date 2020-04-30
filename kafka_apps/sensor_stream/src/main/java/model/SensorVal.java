package model;

/*
* Data holder for sensor values
*/

public class SensorVal {

    private String timestamp;
    private float value;
    
    public SensorVal(String source_timestamp, float source_reading) {
            this.timestamp = source_timestamp;
            this.value = source_reading;
    }


    @Override
    public String toString() {
        
        String stamp = this.timestamp + " " + this.value;
        
        return stamp;
    }


    public void setDatetime(String stamp){
        this.timestamp = stamp;
    }

    public void setReading(Float value){
        this.value = value;
    }

    public String getDatetime() {
        return this.timestamp;
    }

    public Float getReading(){
        return this.value;
    }
    
}