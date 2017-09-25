package accelerometer.client.api;

import accelerometer.client.model.Acceleration;
import accelerometer.client.model.TrainingAcceleration;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

public interface RestApi {

    @POST("/acceleration")
    public Response sendAccelerationValues(@Body Acceleration acceleration);


    @POST("/training")
    public Response sendTrainingValues (@Body TrainingAcceleration trainingAcceleration);
    
}
