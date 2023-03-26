package ucr.example.arce.controllers;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {

    public FileManager() {
    }

    /*
     * Do: escribe datos en un elemento Json dado para el usuario
     * Param: context y un objeto json
     * */
    public void writeData(Context context, JSONObject jso) throws IOException {
        String userString = jso.toString();
        // Define the File Path and its Name
        File file = new File(context.getFilesDir(),"user.json");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(userString);
        bufferedWriter.close();
    }

    /*
     * Do: Lee los datos de un objeto json, en este caso los del usuario
     * Param: context
     * Return: devuelve un objeto json con los datos
     * */
    public JSONObject readData(Context context) throws IOException, JSONException {
        File file = new File(context.getFilesDir(),"user.json");
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line = bufferedReader.readLine();
        while (line != null){
            stringBuilder.append(line).append("\n");
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        // This responce will have Json Format String
        String response = stringBuilder.toString();
        JSONObject jsonObject = new JSONObject(response);
        return jsonObject;
    }
}
