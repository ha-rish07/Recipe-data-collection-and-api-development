package com.example.recipes.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.recipes.model.Recipe;
import com.example.recipes.repository.RecipeRepository;
import com.example.recipes.repository.RecipeSpecification;

@Service
public class RecipeService {

    private final RecipeRepository repository;

    public RecipeService(RecipeRepository repository) {
        this.repository = repository;
    }

    public Page<Recipe> getAll(int page, int limit) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), limit, Sort.by(Sort.Direction.DESC, "rating"));
        return repository.findAll(pageable);
    }

    public List<Recipe> search(String caloriesOp, Integer caloriesVal,
                               String title,
                               String cuisine,
                               String totalTimeOp, Integer totalTimeVal,
                               String ratingOp, Double ratingVal) {

        List<Specification<Recipe>> specs = new ArrayList<>();

        if (title != null && !title.isEmpty()) specs.add(RecipeSpecification.hasTitleLike(title));
        if (cuisine != null && !cuisine.isEmpty()) specs.add(RecipeSpecification.hasCuisine(cuisine));
        if (totalTimeVal != null) specs.add(RecipeSpecification.compareIntegerField("totalTime", totalTimeOp, totalTimeVal));
        if (ratingVal != null) specs.add(RecipeSpecification.compareDoubleField("rating", ratingOp, ratingVal));
        if (caloriesVal != null) specs.add(RecipeSpecification.compareIntegerField("caloriesInt", caloriesOp, caloriesVal));

        Specification<Recipe> finalSpec = null;
        for (Specification<Recipe> s : specs) {
            if (finalSpec == null) finalSpec = Specification.where(s);
            else finalSpec = finalSpec.and(s);
        }

        if (finalSpec == null) return repository.findAll();
        return repository.findAll(finalSpec);
    }
}
