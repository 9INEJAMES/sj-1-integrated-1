package int221.integrated1backend.dtos;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BoardCreateInputDTO {
    @NotNull
    private String name;

    public String getId() {
        return NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, 10);
    }
}
