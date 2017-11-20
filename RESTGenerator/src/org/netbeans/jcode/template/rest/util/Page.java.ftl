package ${package};

public class Page {

    private int number;
    private int size;
    private int totalElements;

    public Page(int number, int size, int totalElements) {
        this.number = number;
        this.size = size;
        this.totalElements = totalElements;
    }

    /**
     * @return the totalElements
     */
    public int getTotalElements() {
        return totalElements;
    }

    /**
     * @param totalElements the totalElements to set
     */
    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }

    /**
     * @param number the number to set
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return the totalPages
     */
    public int getTotalPages() {
        if(size == 0){
            return 0;
        }
        if (totalElements % size == 0) {
            return totalElements / size;
        } else {
            return totalElements / size + 1;
        }
    }

}
