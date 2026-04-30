package dtoRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
public class CreateProgramRequest {

    @JsonProperty("programDescription")
    private String programDescription;
    @JsonProperty("programName")
    private String programName;
    @JsonProperty("programStatus")
    private String programStatus;

    public void setProgramName(String programName) {
        this.programName = programName;
    }
    public String getProgramName() {
        return programName;
    }

    public void setProgramDescription(String programDescription) {
        this.programDescription = programDescription;
    }

    public String getProgramDescription() {
        return programDescription;
    }

    public void setprogramStatus(String programStatus) {
        this.programStatus = programStatus;
    }

    public String getprogramStatus() {
        return programStatus;
    }

}
