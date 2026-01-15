ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.notiz ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.shared_notiz ENABLE ROW LEVEL SECURITY;

-- A Table USERS

-- 1. Erlaube Nutzern, ihr eigenes Profil zu sehen
CREATE POLICY "Nutzer können users Profil lesen"
ON public.users FOR SELECT
    TO public
    USING (true);

-- 2. Erlaube Nutzern, ihr eigenes Profil zu aktualisieren (Wichtig für avatar_url und Namen)
CREATE POLICY "Nutzer können eigenes Profil aktualisieren"
ON public.users FOR UPDATE
    TO authenticated
    USING (auth.uid() = id);

create policy "Users can insert their own profile"
on public.users For insert
    with check (auth.uid() = id);

create policy "Users can delete their own profile"
on public.users For DELETE
    USING (auth.uid() = id);

-- B Table NOTIZ

CREATE POLICY "load the notes"
ON public.notiz FOR SELECT
    TO public
    USING (true);

CREATE POLICY "update the notes"
ON public.notiz FOR UPDATE
    TO authenticated
    USING (auth.uid() = owner_id);

CREATE POLICY "add the notes"
ON public.notiz FOR INSERT
    TO authenticated
    with check (auth.uid() = owner_id);

CREATE POLICY "delete the notes"
ON public.notiz FOR DELETE
    TO authenticated
    USING (auth.uid() = owner_id);

-- C Table SHARED_NOTIZ

CREATE POLICY "load the shared notes"
ON public.shared_notiz FOR SELECT
    TO authenticated
    USING ((auth.uid() = sender_id) OR (auth.uid() = receiver_id));

CREATE POLICY "update the notes"
ON public.shared_notiz FOR UPDATE
    TO authenticated
    USING (auth.uid() = sender_id);

CREATE POLICY "add the notes"
ON public.shared_notiz FOR INSERT
    TO authenticated
    with check (auth.uid() = sender_id);

CREATE POLICY "delete the notes"
ON public.shared_notiz FOR DELETE
TO authenticated
    USING ((auth.uid() = sender_id) OR (auth.uid() = receiver_id));

