package int221.integrated1backend.dtos;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BoardCreateInputDTO {
    @NotNull
    @Size(min = 0, max = 120)
    private String name;
    private Boolean isPublic;

    public String getId() {
        return NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, 10);
    }
}
