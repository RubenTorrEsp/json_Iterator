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

    public static void validarJson(JsonElement jsonReal, JsonElement jsonSchema) {
        if (jsonReal.isJsonObject() && jsonSchema.isJsonObject()) {
            validarJsonObject(jsonReal.getAsJsonObject(), jsonSchema.getAsJsonObject());
        } else if (jsonReal.isJsonArray() && jsonSchema.isJsonArray()) {
            validateJsonArray(jsonReal.getAsJsonArray(), jsonSchema.getAsJsonArray());
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
                    if (!checkType(valorReal, tipoEsperado)) {
                        System.out.println("Error: El campo '" + clave + "' no coincide con el tipo esperado.");
                    }
                } else if (valorEsquema.isJsonObject() && valorReal.isJsonObject()) {
                    validarJsonObject(valorReal.getAsJsonObject(), valorEsquema.getAsJsonObject());
                } else if (valorEsquema.isJsonArray() && valorReal.isJsonArray()) {
                    validateJsonArray(valorReal.getAsJsonArray(), valorEsquema.getAsJsonArray());
                } else {
                    System.out.println("Error: Estructura inesperada o no coincidente para el campo '" + clave + "'.");
                }
            } else {
                System.out.println("Error: Falta el campo esperado '" + clave + "' en el JSON real.");
            }
        }
    }

    private static void validateJsonArray(JsonArray arrayReal, JsonArray arrayScchema) {
        if (!arrayScchema.isEmpty()) {
            JsonElement schemaType = arrayScchema.get(0);
            for (JsonElement element : arrayReal) {
                if (schemaType.isJsonPrimitive()) {
                    String typeExpected = schemaType.getAsString();
                    if (!checkType(element, typeExpected)) {
                        System.out.println("Error en array: El tipo de elemento no coincide con el tipo esperado.");
                    }
                } else if (schemaType.isJsonObject() && element.isJsonObject()) {
                    validarJsonObject(element.getAsJsonObject(), schemaType.getAsJsonObject());
                } else if (schemaType.isJsonArray() && element.isJsonArray()) {
                    validateJsonArray(element.getAsJsonArray(), schemaType.getAsJsonArray());
                } else {
                    System.out.println("Error en array: Estructura inesperada o no coincidente en el array.");
                }
            }
        }
    }

    private static boolean checkType(JsonElement valor, String tipoEsperado) {
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

}
