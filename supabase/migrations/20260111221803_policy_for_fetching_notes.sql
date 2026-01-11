CREATE POLICY "fetch note" 
ON public.notiz FOR SELECT
TO public
USING (true);
