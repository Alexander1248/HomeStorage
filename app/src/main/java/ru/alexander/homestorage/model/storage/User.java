package ru.alexander.homestorage.model.storage;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    private String uid;
    private boolean canAdd;
    private boolean canEdit;
    private boolean canEditAny;
    private boolean canDelete;
    private boolean canDeleteAny;
    private boolean canAdmin;
    private boolean canAdminAny;

    public User() {
    }

    private User(String uid,
                boolean canAdd,
                boolean canEdit, boolean canEditAny,
                boolean canDelete, boolean canDeleteAny,
                boolean canAdmin, boolean canAdminAny) {
        this.uid = uid;
        this.canAdd = canAdd;
        this.canEdit = canEdit;
        this.canEditAny = canEditAny;
        this.canDelete = canDelete;
        this.canDeleteAny = canDeleteAny;
        this.canAdmin = canAdmin;
        this.canAdminAny = canAdminAny;
    }

    @PropertyName("add")
    public void setCanAdd(boolean canAdd) {
        this.canAdd = canAdd;
    }

    @PropertyName("edit")
    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    @PropertyName("edit_any")
    public void setCanEditAny(boolean canEditAny) {
        this.canEditAny = canEditAny;
    }

    @PropertyName("delete")
    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    @PropertyName("delete_any")
    public void setCanDeleteAny(boolean canDeleteAny) {
        this.canDeleteAny = canDeleteAny;
    }

    @PropertyName("admin")
    public void setCanAdmin(boolean canAdmin) {
        this.canAdmin = canAdmin;
    }

    @PropertyName("admin_any")
    public void setCanAdminAny(boolean canAdminAny) {
        this.canAdminAny = canAdminAny;
    }

    @PropertyName("uid")
    public String getUid() {
        return uid;
    }

    @PropertyName("add")
    public boolean isCanAdd() {
        return canAdd;
    }

    @PropertyName("edit")
    public boolean isCanEdit() {
        return canEdit;
    }

    @PropertyName("edit_any")
    public boolean isCanEditAny() {
        return canEditAny;
    }

    @PropertyName("delete")
    public boolean isCanDelete() {
        return canDelete;
    }

    @PropertyName("delete_any")
    public boolean isCanDeleteAny() {
        return canDeleteAny;
    }

    @PropertyName("admin")
    public boolean isCanAdmin() {
        return canAdmin;
    }

    @PropertyName("admin_any")
    public boolean isCanAdminAny() {
        return canAdminAny;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(uid, user.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }

    public static class Builder {
        private final String uid;
        private boolean canAdd;
        private boolean canEdit;
        private boolean canEditAny;
        private boolean canDelete;
        private boolean canDeleteAny;
        private boolean canAdmin;
        private boolean canAdminAny;

        public Builder(String uid) {
            this.uid = uid;
        }

        public Builder setCanAdd(boolean canAdd) {
            this.canAdd = canAdd;
            return this;
        }

        public Builder setCanEdit(boolean canEdit) {
            this.canEdit = canEdit;
            return this;
        }

        public Builder setCanEditAny(boolean canEditAny) {
            this.canEditAny = canEditAny;
            return this;
        }

        public Builder setCanDelete(boolean canDelete) {
            this.canDelete = canDelete;
            return this;
        }

        public Builder setCanDeleteAny(boolean canDeleteAny) {
            this.canDeleteAny = canDeleteAny;
            return this;
        }

        public Builder setCanAdmin(boolean canAdmin) {
            this.canAdmin = canAdmin;
            return this;
        }

        public Builder setCanAdminAny(boolean canAdminAny) {
            this.canAdminAny = canAdminAny;
            return this;
        }

        public Builder editor() {
            setCanAdd(true);
            setCanEdit(true);
            setCanDelete(true);
            return this;
        }
        public Builder administrator() {
            editor();

            setCanEditAny(true);
            setCanDeleteAny(true);
            setCanAdmin(true);
            return this;
        }

        public Builder creator() {
            administrator();

            setCanAdminAny(true);
            return this;
        }

        public User build() {
            return new User(uid, canAdd, canEdit, canEditAny, canDelete, canDeleteAny, canAdmin, canAdminAny);
        }
    }
}


