package entrenador.entrenador.assembler;

import entrenador.entrenador.controller.EntrenadorController;
import entrenador.entrenador.dto.EntrenadorResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EntrenadorModelAssembler implements RepresentationModelAssembler<EntrenadorResponseDTO, EntityModel<EntrenadorResponseDTO>> {
    @Override
    public EntityModel<EntrenadorResponseDTO> toModel(EntrenadorResponseDTO dto) {
        return EntityModel.of(
                dto,
                linkTo(methodOn(EntrenadorController.class).obtenerEntrenador(dto.getId())).withSelfRel(),
                linkTo(methodOn(EntrenadorController.class).eliminar(dto.getId())).withRel("delete")
        );
    }
}
