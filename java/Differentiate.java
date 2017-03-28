/**
 * TODO:
 * - implement expression tree
 */

class TreeNode {
    TreeNode parent;
    TreeNode left, right;

    TreeNode(Data data, TreeNode left, TreeNode right) {
        this.data = data;

        if (left != null) {
            this.left = left;
            left.parent = this;
        }

        if (right != null) {
            this.right = right;
            right.parent = this;
        }
    }

}

enum OperationType {
    ADD,
    SUBTRACT, // could probably do away with this, 
              // since subtraction is negative adding
    MULTIPLY,
    DIVIDE,
    POWER;
}

class Operation extends TreeNode {
    OperationType type;
    Operand left, right;

    Operation(OperationType type, Operand left, Operand right) {
        this.type = type;
        this.left = left;
        this.right = right;
    }


}

// only variables for now, TODO add functions (sin/cos, log, etc.)
class Operand {
    OperandType type;
    String stringValue;
    int intValue = -1;

    Operand(OperandType type) {
        this.type = type;
    }

    Operand getDerivative() {
        if (this.type == OperandType.CONSTANT) {
            if (intValue)
            Operand o = new Operand(OperandType.CONSTANT);
        }

        if (this.type == OperandType.VARIABLE) {
            Operand o = new Operand(OperandType.CONSTANT);
            o.intValue = 1;
            return o;
        }
    }
}

enum OperandType {
    CONSTANT,
    VARIABLE,
    FUNCTION;
}

class ExpressionTree {
    TreeNode root;


}

class Main {
    public static void main(String[] args) {
        
    }
}