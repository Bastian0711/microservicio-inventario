package cl.duoc.backend_inventario.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cl.duoc.backend_inventario.dto.InventarioDTO;
import cl.duoc.backend_inventario.service.InventarioService;

class InventarioControllerTest {

    private final InventarioService service = mock(InventarioService.class);
    private final InventarioController controller = new InventarioController(service);
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

    @Test
    void testListarRetorna200() throws Exception {
        when(service.listar()).thenReturn(List.of());

        mockMvc.perform(get("/api/v2/inventario"))
                .andExpect(status().isOk());
    }

    @Test
    void testObtenerPorIdRetorna200() throws Exception {
        InventarioDTO dto = new InventarioDTO(1L, 1L, "Sushi Combo", "Japonesa", 20, 8500.0);

        when(service.obtenerPorId(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/v2/inventario/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testEliminarRetorna200() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/v2/inventario/1"))
                .andExpect(status().isOk());
    }
}