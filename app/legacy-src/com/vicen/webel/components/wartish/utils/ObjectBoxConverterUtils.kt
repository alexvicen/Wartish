package com.vicen.webel.components.wartish.utils

import com.google.gson.Gson
import io.objectbox.converter.PropertyConverter

class ObjectBoxConverterUtils {
    companion object {

        class MapConverter : PropertyConverter<Map<String, String>, String> {
            override fun convertToDatabaseValue(entityProperty: Map<String, String>?): String {
                return entityProperty?.let { Gson().toJson(entityProperty) } ?: ""
            }

            @Suppress("UNCHECKED_CAST")
            override fun convertToEntityProperty(databaseValue: String?): Map<String, String> {
                return if (databaseValue.isNullOrBlank()) mutableMapOf()
                else Gson().fromJson(databaseValue, MutableMap::class.java) as MutableMap<String, String>
            }
        }

        class ArrayListConverter : PropertyConverter<MutableList<String>, String> {
            override fun convertToDatabaseValue(entityProperty: MutableList<String>?): String {
                return entityProperty?.let { Gson().toJson(entityProperty) } ?: ""
            }

            @Suppress("UNCHECKED_CAST")
            override fun convertToEntityProperty(databaseValue: String?): MutableList<String> {
                return if (databaseValue.isNullOrBlank()) mutableListOf()
                else Gson().fromJson(databaseValue, MutableList::class.java) as MutableList<String>
            }
        }

    }
}