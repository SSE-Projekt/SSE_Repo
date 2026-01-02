import mdiIcons from '@/assets/icons/mdi-icons.json';

const iconMap = Object.fromEntries(mdiIcons.map(i => [i.name, i.path]));

export const getIcon = (name) => {
    return iconMap[name] || '';
};