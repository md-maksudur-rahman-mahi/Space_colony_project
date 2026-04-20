package com.example.spacecolony.manager;

import android.content.Context;
import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Engineer;
import com.example.spacecolony.model.Medic;
import com.example.spacecolony.model.Pilot;
import com.example.spacecolony.model.Scientist;
import com.example.spacecolony.model.Soldier;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;

public class DataManager {
    private static final String FILENAME = "space_colony_save.json";
    private static DataManager instance;
    private final Context context;

    private DataManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized DataManager getInstance(Context context) {
        if (instance == null) instance = new DataManager(context);
        return instance;
    }

    public void saveData() {
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(CrewMember.class, new CrewMemberSerializer())
                    .setPrettyPrinting()
                    .create();

            Map<Integer, CrewMember> map = Storage.getInstance().getMap();
            String json = gson.toJson(map);

            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadData() {
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            br.close();

            String json = sb.toString();
            if (json.isEmpty()) return;

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(CrewMember.class, new CrewMemberDeserializer())
                    .create();

            Type type = new TypeToken<Map<Integer, CrewMember>>(){}.getType();
            Map<Integer, CrewMember> loadedMap = gson.fromJson(json, type);

            Storage.getInstance().getMap().clear();
            Storage.getInstance().getMap().putAll(loadedMap);

            int maxId = 0;
            for (int id : loadedMap.keySet()) if (id > maxId) maxId = id;
            Storage.getInstance().setNextId(maxId + 1);
        } catch (IOException e) {
            // First run
        }
    }

    private static class CrewMemberSerializer implements JsonSerializer<CrewMember> {
        @Override
        public JsonElement serialize(CrewMember src, Type typeOfSrc, JsonSerializationContext context) {
            JsonElement element = context.serialize(src);
            element.getAsJsonObject().addProperty("specialization", src.getSpecialization());
            return element;
        }
    }

    private static class CrewMemberDeserializer implements JsonDeserializer<CrewMember> {
        @Override
        public CrewMember deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String spec = json.getAsJsonObject().get("specialization").getAsString();
            switch (spec) {
                case "Pilot": return context.deserialize(json, Pilot.class);
                case "Engineer": return context.deserialize(json, Engineer.class);
                case "Medic": return context.deserialize(json, Medic.class);
                case "Scientist": return context.deserialize(json, Scientist.class);
                case "Soldier": return context.deserialize(json, Soldier.class);
                default: return null;
            }
        }
    }
}