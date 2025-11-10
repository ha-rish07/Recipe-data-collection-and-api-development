package com.example.recipes.controller;

import com.example.recipes.model.Recipe;
import com.example.recipes.service.RecipeService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService service;

    public RecipeController(RecipeService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(value = "page", defaultValue = "1") int page,
                                    @RequestParam(value = "limit", defaultValue = "10") int limit) {
        Page<Recipe> p = service.getAll(page, limit);
        Map<String, Object> resp = new HashMap<>();
        resp.put("page", page);
        resp.put("limit", limit);
        resp.put("total", p.getTotalElements());
        resp.put("data", p.getContent());
        return ResponseEntity.ok(resp);
    }

    // Example: calories=<=400, title=pie, rating=>=4.5
    @GetMapping("/search")
    public ResponseEntity<?> search(
                                    @RequestParam(value = "calories", required = false) String calories,
                                    @RequestParam(value = "title", required = false) String title,
                                    @RequestParam(value = "cuisine", required = false) String cuisine,
                                    @RequestParam(value = "total_time", required = false) String totalTime,
                                    @RequestParam(value = "rating", required = false) String rating) {

        OperatorValue cal = parseOperatorValue(calories);
        OperatorValue tot = parseOperatorValue(totalTime);
        OperatorValue rat = parseOperatorValueDouble(rating);

        List<Recipe> results = service.search(
                cal == null ? null : cal.operator,
                cal == null ? null : cal.intValue,
                title,
                cuisine,
                tot == null ? null : tot.operator,
                tot == null ? null : tot.intValue,
                rat == null ? null : rat.operator,
                rat == null ? null : rat.doubleValue
        );

        Map<String, Object> resp = new HashMap<>();
        resp.put("data", results);
        return ResponseEntity.ok(resp);
    }

    static class OperatorValue {
        String operator;
        Integer intValue;
        Double doubleValue;
    }

    private OperatorValue parseOperatorValue(String raw) {
        if (raw == null || raw.isEmpty()) return null;
        OperatorValue ov = new OperatorValue();
        String op = null;
        String val = raw;
        if (raw.startsWith("<=") || raw.startsWith(">=")) {
            op = raw.substring(0, 2);
            val = raw.substring(2);
        } else if (raw.startsWith("<") || raw.startsWith(">") || raw.startsWith("=")) {
            op = raw.substring(0, 1);
            val = raw.substring(1);
        } else {
            op = "=";
        }
        ov.operator = op;
        try {
            ov.intValue = Integer.parseInt(val.replaceAll("[^0-9-]", ""));
        } catch (Exception e) {
            ov.intValue = null;
        }
        return ov;
    }

    private OperatorValue parseOperatorValueDouble(String raw) {
        if (raw == null || raw.isEmpty()) return null;
        OperatorValue ov = new OperatorValue();
        String op = null;
        String val = raw;
        if (raw.startsWith("<=") || raw.startsWith(">=")) {
            op = raw.substring(0, 2);
            val = raw.substring(2);
        } else if (raw.startsWith("<") || raw.startsWith(">") || raw.startsWith("=")) {
            op = raw.substring(0, 1);
            val = raw.substring(1);
        } else {
            op = "=";
        }
        ov.operator = op;
        try {
                ov.doubleValue = Double.parseDouble(val.replaceAll("[^0-9.-]", ""));
        } catch (Exception e) {
            ov.doubleValue = null;
        }
        return ov;
    }
}
