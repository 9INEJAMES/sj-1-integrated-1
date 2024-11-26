package int221.integrated1backend.dtos;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import int221.integrated1backend.utils.Constant;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BoardCreateInputDTO {
    @NotNull
    @Size(min = 0, max = Constant.BOARD_NAME_LIMIT)
    private String name;

    public String getId() {
        return NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, 10);
    }
}
