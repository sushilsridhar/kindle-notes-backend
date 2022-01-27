package com.amazonaws.lambda.kindle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.plivo.api.Plivo;
import com.plivo.api.exceptions.PlivoRestException;
import com.plivo.api.models.message.Message;

public class LambdaFunctionHandler implements RequestHandler<Object, String> {

    @Override
    public String handleRequest(Object input, Context context) {
        context.getLogger().log("Input: " + input);

        // TODO: implement your handler
        AWSCredentials credentials = new BasicAWSCredentials("AKIAIBMJUCJPS6EAJOJQ","2UguiFGwIVkp7myc7tyrV0AUvKwxcauJDttC4b+U");
		
		AmazonS3 s3Client = AmazonS3ClientBuilder
				.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(Regions.US_EAST_2)
				.build();
		
		S3Object object = s3Client.getObject(new GetObjectRequest("kindle-notes-bucket","kindlenotes.txt"));
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(object.getObjectContent()));

		List<String> list = (List<String>) reader.lines().collect(Collectors.toList());
		
		String text = list.get(new Random().nextInt(list.size()));
		
		Plivo.init("MAMMEYMJA1NTAXNZFKNT","M2VhNzQyNDkyYmU2ODM0ZjY3YTc1Y2IwZjM0MDE3");
		try {
			Message.creator("SourcePhoneNumber", Collections.singletonList("ReceiverPhoneNumber"), text)
			.create();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PlivoRestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
        return text;
    }

}
