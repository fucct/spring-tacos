package tacos;

import static tacos.Ingredient.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/design")
@AllArgsConstructor
public class DesignTacoController {

    @Autowired
    private final IngredientRepository ingredientRepository;

    @Autowired
    private final TacoRepository tacoRepository;

    @ModelAttribute(name="order")
    public Order order(){
        return new Order();
    }

    @ModelAttribute(name="taco")
    public Taco taco(){
        return new Taco();
    }

    @GetMapping
    public String showDesignForm(Model model) {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredientRepository.findAll().forEach(ingredients::add);

        Type[] types = Type.values();
        for (Type type : types) {
            model.addAttribute(type.name().toLowerCase(), filterByType(ingredients, type));
        }
        model.addAttribute("taco", new Taco());

        return "design";
    }

    @PostMapping
    public String processDesign(@Valid Taco taco, Errors errors, @ModelAttribute Order order) {
        if (errors.hasErrors()) {
            return "design";
        }
        log.info("Processing design: " + taco);
        Taco saved = tacoRepository.save(taco);

        return "redirect:/orders/current";
    }

    private List<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {
        return ingredients
            .stream()
            .filter(x -> x.getType().equals(type))
            .collect(Collectors.toList());
    }
}
