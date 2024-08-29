package org.example;

import com.google.gson.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class ComprobadorTemplate {

    public ComprobadorTemplate(String rutaJsonReal, String rutaJsonSchema) {

        try {
            FileReader readerReal = new FileReader(rutaJsonReal);
            FileReader readerEsquema = new FileReader(rutaJsonSchema);

            JsonElement jsonReal = JsonParser.parseReader(readerReal);
            JsonElement jsonEsquema = JsonParser.parseReader(readerEsquema);

            validarJson(jsonReal, jsonEsquema);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void validarJson(JsonElement jsonReal, JsonElement jsonEsquema) {
        if (jsonReal.isJsonObject() && jsonEsquema.isJsonObject()) {
            validarJsonObject(jsonReal.getAsJsonObject(), jsonEsquema.getAsJsonObject());
        } else if (jsonReal.isJsonArray() && jsonEsquema.isJsonArray()) {
            validarJsonArray(jsonReal.getAsJsonArray(), jsonEsquema.getAsJsonArray());
        } else {
            System.out.println("Error: Los elementos JSON no son del mismo tipo o no son objetos ni arrays.");
        }
    }

    private static void validarJsonObject(JsonObject objReal, JsonObject objEsquema) {
        for (Map.Entry<String, JsonElement> entrada : objEsquema.entrySet()) {
            String clave = entrada.getKey();
            JsonElement valorEsquema = entrada.getValue();

            if (objReal.has(clave)) {
                JsonElement valorReal = objReal.get(clave);

                if (valorEsquema.isJsonPrimitive()) {
                    String tipoEsperado = valorEsquema.getAsString();
                    if (!comprobarTipo(valorReal, tipoEsperado)) {
                        System.out.println("Error: El campo '" + clave + "' no coincide con el tipo esperado. Tipo esperado: " + tipoEsperado + ", Tipo encontrado: " + obtenerTipo(valorReal));
                    }
                } else if (valorEsquema.isJsonObject() && valorReal.isJsonObject()) {
                    // Validar recursivamente el objeto JSON anidado
                    validarJsonObject(valorReal.getAsJsonObject(), valorEsquema.getAsJsonObject());
                } else if (valorEsquema.isJsonArray() && valorReal.isJsonArray()) {
                    // Validar recursivamente el array JSON anidado
                    validarJsonArray(valorReal.getAsJsonArray(), valorEsquema.getAsJsonArray());
                } else {
                    System.out.println("Error: Estructura inesperada o no coincidente para el campo '" + clave + "'.");
                }
            } else {
                System.out.println("Error: Falta el campo esperado '" + clave + "' en el JSON real.");
            }
        }
    }

    private static void validarJsonArray(JsonArray arrayReal, JsonArray arrayEsquema) {
        if (!arrayEsquema.isEmpty()) {
            JsonElement tipoEsquema = arrayEsquema.get(0); // Suponemos que todos los elementos del array son del mismo tipo
            for (JsonElement elementoReal : arrayReal) {
                if (tipoEsquema.isJsonPrimitive()) {
                    String tipoEsperado = tipoEsquema.getAsString();
                    if (!comprobarTipo(elementoReal, tipoEsperado)) {
                        System.out.println("Error en array: El tipo de elemento no coincide con el tipo esperado. Tipo esperado: " + tipoEsperado + ", Tipo encontrado: " + obtenerTipo(elementoReal));
                    }
                } else if (tipoEsquema.isJsonObject() && elementoReal.isJsonObject()) {
                    // Validar recursivamente el objeto JSON dentro del array
                    validarJsonObject(elementoReal.getAsJsonObject(), tipoEsquema.getAsJsonObject());
                } else if (tipoEsquema.isJsonArray() && elementoReal.isJsonArray()) {
                    // Validar recursivamente el array JSON anidado dentro del array
                    validarJsonArray(elementoReal.getAsJsonArray(), tipoEsquema.getAsJsonArray());
                } else {
                    System.out.println("Error en array: Estructura inesperada o no coincidente en el array.");
                }
            }
        }
    }

    private static boolean comprobarTipo(JsonElement valor, String tipoEsperado) {
        return switch (tipoEsperado.toLowerCase()) {
            case "string" -> valor.isJsonPrimitive() && valor.getAsJsonPrimitive().isString();
            case "boolean" -> valor.isJsonPrimitive() && valor.getAsJsonPrimitive().isBoolean();
            case "number" -> valor.isJsonPrimitive() && valor.getAsJsonPrimitive().isNumber();
            default -> {
                System.out.println("Error: Tipo desconocido en el esquema JSON: " + tipoEsperado);
                yield false;
            }
        };
    }

    private static String obtenerTipo(JsonElement valor) {
        if (valor.isJsonPrimitive()) {
            JsonPrimitive primitivo = valor.getAsJsonPrimitive();
            if (primitivo.isString()) {
                return "string";
            } else if (primitivo.isBoolean()) {
                return "boolean";
            } else if (primitivo.isNumber()) {
                return "number";
            }
        } else if (valor.isJsonObject()) {
            return "object";
        } else if (valor.isJsonArray()) {
            return "array";
        }
        return "unknown";
    }
}
