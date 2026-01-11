CREATE POLICY "update notes" 
ON public.notiz FOR UPDATE
TO authenticated 
USING (auth.uid() = owner_id);


CREATE POLICY "delete notes" 
ON public.notiz FOR DELETE
TO authenticated 
USING (auth.uid() = owner_id);