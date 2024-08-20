package org.example;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String rutaJson = "E:\\Proyectos\\json_iterator\\src\\main\\java\\org\\example\\colores.json";
        String claveLista = "miLista";  // Reemplaza con la clave de la lista que quieres verificar

        try {
            FileReader reader = new FileReader(rutaJson);
            JsonElement jsonElement = JsonParser.parseReader(reader);
            buscarCampoVacioEnLista(jsonElement, claveLista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void buscarCampoVacioEnLista(JsonElement elemento, String claveLista) {
        if (elemento.isJsonObject()) {
            JsonObject objeto = elemento.getAsJsonObject();

            // Verificar si el objeto contiene la lista que buscamos
            if (objeto.has(claveLista)) {
                JsonElement lista = objeto.get(claveLista);

                if (lista.isJsonArray()) {
                    JsonArray jsonArray = lista.getAsJsonArray();
                    for (JsonElement item : jsonArray) {
                        buscarCampoVacio(item);
                    }
                } else {
                    System.out.println("El campo '" + claveLista + "' no es un array.");
                }
            } else {
                System.out.println("El campo '" + claveLista + "' no existe en el JSON.");
            }
        }
    }

    private static void buscarCampoVacio(JsonElement elemento) {
        if (elemento.isJsonObject()) {
            JsonObject objeto = elemento.getAsJsonObject();
            for (String key : objeto.keySet()) {
                JsonElement valor = objeto.get(key);

                // Verificar si el campo es null
                if (valor.isJsonNull()) {
                    System.out.println("El campo '" + key + "' tiene un valor null.");
                }
                // Verificar si el campo es una cadena vacía
                else if (valor.isJsonPrimitive() && valor.getAsJsonPrimitive().isString() && valor.getAsString().isEmpty()) {
                    System.out.println("El campo '" + key + "' está vacío (cadena vacía).");
                }
                // Verificar si el campo es un array vacío
                else if (valor.isJsonArray() && valor.getAsJsonArray().isEmpty()) {
                    System.out.println("El campo '" + key + "' está vacío (array vacío).");
                }
                // Verificar si el campo es un objeto vacío
                else if (valor.isJsonObject() && valor.getAsJsonObject().size() == 0) {
                    System.out.println("El campo '" + key + "' está vacío (objeto JSON vacío).");
                }
                else {
                    buscarCampoVacio(valor);
                }
            }
        } else if (elemento.isJsonArray()) {
            JsonArray array = elemento.getAsJsonArray();
            for (JsonElement item : array) {
                buscarCampoVacio(item);
            }
        }
    }
}