package tacos;

import java.util.Date;
import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Relation(itemRelation = "taco", collectionRelation = "tacos")
public class TacoModel extends RepresentationModel<TacoModel> {

    private static final IngredientModelAssembler assembler = new IngredientModelAssembler();

    private final String name;

    private final Date createdAt;

    private final CollectionModel<IngredientModel> ingredients;

    public TacoModel(Taco taco) {
        this.name = taco.getName();
        this.createdAt = taco.getCreatedAt();
        this.ingredients = assembler.toCollectionModel(taco.getIngredients());
    }

}
