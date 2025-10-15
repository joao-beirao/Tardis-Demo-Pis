package dcr.model;

public abstract class GenericElement
    implements ModelElement {
  // private final String choreoElementUID;
  private final String endpointElementUID;

  // TODO seal

  // public GenericElement(String choreoElementUID, String endpointElementUID) {
  public GenericElement(String endpointElementUID) {
    // this.choreoElementUID = choreoElementUID;
      this.endpointElementUID = endpointElementUID;
  }

  @Override
  public String endpointElementUID() {
    return endpointElementUID;
  }

  // public String choreoElementUID() {
  //   return choreoElementUID;
  // }
}
