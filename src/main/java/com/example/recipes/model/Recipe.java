package com.example.recipes.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String cuisine;

    private String title;

    private Double rating;

    private Integer prepTime;

    private Integer cookTime;

    private Integer totalTime;

    @Column(columnDefinition = "text")
    private String description;

    
    @Column(name = "nutrients", columnDefinition = "text")
    private String nutrientsJson;

    @Transient
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String serves;

    private Integer caloriesInt;

    public Recipe() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(Integer prepTime) {
        this.prepTime = prepTime;
    }

    public Integer getCookTime() {
        return cookTime;
    }

    public void setCookTime(Integer cookTime) {
        this.cookTime = cookTime;
    }

    public Integer getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Integer totalTime) {
        this.totalTime = totalTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public JsonNode getNutrients() {
        if (nutrientsJson == null) return null;
        try {
            return MAPPER.readTree(nutrientsJson);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public void setNutrients(JsonNode nutrients) {
        if (nutrients == null) {
            this.nutrientsJson = null;
            return;
        }
        try {
            this.nutrientsJson = MAPPER.writeValueAsString(nutrients);
        } catch (JsonProcessingException e) {
            this.nutrientsJson = nutrients.toString();
        }
    }

    
    public String getNutrientsJson() { return nutrientsJson; }
    public void setNutrientsJson(String nutrientsJson) { this.nutrientsJson = nutrientsJson; }

    public String getServes() {
        return serves;
    }

    public void setServes(String serves) {
        this.serves = serves;
    }

    public Integer getCaloriesInt() {
        return caloriesInt;
    }

    public void setCaloriesInt(Integer caloriesInt) {
        this.caloriesInt = caloriesInt;
    }
}
