package fr.inria.lille.repair.nopol.ifmetric;

class IfPosition {
    private String className;
    private int position;

    private IfPosition(String className, int position) {
        this.className = className;
        this.position = position;
    }

    public static IfPosition create(String className, int ifLine) {
        return new IfPosition(className, ifLine);
    }


    public int getPosition() {
        return position;
    }

    public String toString() {
        return className + ":" + position;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((className == null) ? 0 : className.hashCode());
        result = prime * result + position;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IfPosition other = (IfPosition) obj;
        if (className == null) {
            if (other.className != null)
                return false;
        } else if (!className.equals(other.className))
            return false;
        if (position != other.position)
            return false;
        return true;
    }


}