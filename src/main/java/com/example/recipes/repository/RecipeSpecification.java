package com.example.recipes.repository;

import com.example.recipes.model.Recipe;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Expression;

public class RecipeSpecification {

    public static Specification<Recipe> hasTitleLike(String title) {
        return (root, query, cb) -> title == null ? null : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Recipe> hasCuisine(String cuisine) {
        return (root, query, cb) -> cuisine == null ? null : cb.equal(cb.lower(root.get("cuisine")), cuisine.toLowerCase());
    }

    public static Specification<Recipe> compareIntegerField(String fieldName, String operator, Integer value) {
        return (root, query, cb) -> {
            if (value == null) return null;
            Expression<Integer> expr = root.get(fieldName);
            switch (operator) {
                case ">":
                    return cb.greaterThan(expr, value);
                case ">=":
                    return cb.greaterThanOrEqualTo(expr, value);
                case "<":
                    return cb.lessThan(expr, value);
                case "<=":
                    return cb.lessThanOrEqualTo(expr, value);
                default:
                    return cb.equal(expr, value);
            }
        };
    }

    public static Specification<Recipe> compareDoubleField(String fieldName, String operator, Double value) {
        return (root, query, cb) -> {
            if (value == null) return null;
            Expression<Double> expr = root.get(fieldName);
            switch (operator) {
                case ">":
                    return cb.greaterThan(expr, value);
                case ">=":
                    return cb.greaterThanOrEqualTo(expr, value);
                case "<":
                    return cb.lessThan(expr, value);
                case "<=":
                    return cb.lessThanOrEqualTo(expr, value);
                default:
                    return cb.equal(expr, value);
            }
        };
    }
}
