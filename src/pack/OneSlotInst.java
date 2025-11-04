package pack;

import org.objectweb.asm.tree.AbstractInsnNode;

public class OneSlotInst extends StackInst {
    public OneSlotInst(AbstractInsnNode inst) {
        this.inst = inst;
    }
}
