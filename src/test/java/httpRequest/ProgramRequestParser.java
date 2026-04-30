package httpRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dtoRequest.CreateProgramRequest;

public class ProgramRequestParser {
    public static CreateProgramRequest createProgramParseData(String body) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        CreateProgramRequest createProgramParseData =mapper.readValue(body, CreateProgramRequest.class );
        return createProgramParseData;
    }
}
