package tacos.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tacos.Taco;
import tacos.TacoModel;
import tacos.TacoModelAssembler;
import tacos.data.TacoRepository;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RepositoryRestController
public class DesignTacoController {

    private final TacoRepository tacoRepository;

    private final EntityLinks entityLinks;

    @GetMapping(value = "/tacos/recent", produces = "application/hal+json")
    public CollectionModel<TacoModel> getRecentTacos() {
        PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
        List<Taco> tacos = tacoRepository.findAll(page).getContent();

        CollectionModel<TacoModel> tacoModels = new TacoModelAssembler().toCollectionModel(tacos);
        tacoModels.add(
            linkTo(methodOn(DesignTacoController.class).getRecentTacos())
            .withRel("recents"));
        return tacoModels;
    }

    @GetMapping("/{id}")
    public ResponseEntity tacoById(@PathVariable("id") Long id) {
        Optional<Taco> taco = tacoRepository.findById(id);
        if (taco.isPresent()) {
            return ResponseEntity.ok(taco.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Taco postTaco(@RequestBody Taco taco) {
        return tacoRepository.save(taco);
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable("orderId") Long orderId) {
        try {
            tacoRepository.deleteById(orderId);
        } catch (Exception e) {
        }
    }

    @Bean
    public RepresentationModelProcessor<PagedModel<EntityModel<Taco>>> tacoProcessor(
        EntityLinks links) {
        return new RepresentationModelProcessor<PagedModel<EntityModel<Taco>>>() {
            @Override
            public PagedModel<EntityModel<Taco>> process(PagedModel<EntityModel<Taco>> model) {
                model.add(
                    links.linkFor(Taco.class)
                    .slash("recent")
                    .withRel("recents")
                );
                return model;
            }
        };
    }

}
