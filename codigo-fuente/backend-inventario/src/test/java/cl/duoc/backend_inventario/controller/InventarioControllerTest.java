package cl.duoc.backend_inventario.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duoc.backend_inventario.dto.DescontarStockDTO;
import cl.duoc.backend_inventario.dto.InventarioCreateDTO;
import cl.duoc.backend_inventario.dto.InventarioDTO;
import cl.duoc.backend_inventario.dto.InventarioUpdateDTO;
import cl.duoc.backend_inventario.dto.StockResponseDTO;
import cl.duoc.backend_inventario.exception.GlobalExceptionHandler;
import cl.duoc.backend_inventario.exception.RecursoNoEncontradoException;
import cl.duoc.backend_inventario.service.InventarioService;

class InventarioControllerTest {

    private final InventarioService service = mock(InventarioService.class);
    private final InventarioController controller = new InventarioController(service);
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testListarRetorna200() throws Exception {
        when(service.listar()).thenReturn(List.of());

        mockMvc.perform(get("/api/v3/inventario"))
                .andExpect(status().isOk());
    }

    @Test
    void testObtenerPorIdRetorna200() throws Exception {
        InventarioDTO dto = new InventarioDTO(1L, 1L, "Sushi Combo", "Japonesa", 20, 8500.0);

        when(service.obtenerPorId(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/v3/inventario/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testObtenerPorIdNoExistenteRetorna404() throws Exception {
        when(service.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("Inventario no encontrado"));

        mockMvc.perform(get("/api/v3/inventario/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void testObtenerPorProductoRetorna200() throws Exception {
        InventarioDTO dto = new InventarioDTO(1L, 10L, "Sushi Combo", "Japonesa", 20, 8500.0);

        when(service.obtenerPorProducto(10L)).thenReturn(dto);

        mockMvc.perform(get("/api/v3/inventario/producto/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreProducto").value("Sushi Combo"));
    }

    @Test
    void testCrearRetorna201() throws Exception {
        InventarioCreateDTO request = new InventarioCreateDTO();
        request.setIdProducto(1L);
        request.setNombreProducto("Sushi Combo");
        request.setCategoria("Japonesa");
        request.setStock(20);
        request.setPrecio(8500.0);

        InventarioDTO response = new InventarioDTO(1L, 1L, "Sushi Combo", "Japonesa", 20, 8500.0);

        when(service.crearInventario(org.mockito.ArgumentMatchers.any(InventarioCreateDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v3/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreProducto").value("Sushi Combo"));
    }

    @Test
    void testDescontarStockRetorna200() throws Exception {
        DescontarStockDTO request = new DescontarStockDTO();
        request.setIdProducto(1L);
        request.setCantidad(5);

        StockResponseDTO response = new StockResponseDTO(15);

        when(service.descontarStock(org.mockito.ArgumentMatchers.any(DescontarStockDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v3/inventario/descontar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockRestante").value(15));
    }

    @Test
    void testActualizarRetorna200() throws Exception {
        InventarioUpdateDTO request = new InventarioUpdateDTO();
        request.setStock(35);

        InventarioDTO response = new InventarioDTO(1L, 1L, "Sushi Combo", "Japonesa", 35, 8500.0);

        when(service.actualizar(org.mockito.ArgumentMatchers.eq(1L),
                        org.mockito.ArgumentMatchers.any(InventarioUpdateDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v3/inventario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(35));
    }

    @Test
    void testEliminarRetorna200() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/v3/inventario/1"))
                .andExpect(status().isOk());
    }
}