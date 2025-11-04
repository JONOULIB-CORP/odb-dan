package pack;

import org.objectweb.asm.tree.AbstractInsnNode;

public class TwoSlotInst extends StackInst {
    public TwoSlotInst(AbstractInsnNode inst) {
        this.inst = inst;
    }
}
