alter table Role add column institution_uuid char(36) references Institution(uuid);