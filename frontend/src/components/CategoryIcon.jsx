import {
  FaBolt,
  FaBriefcaseMedical,
  FaBus,
  FaCarSide,
  FaGamepad,
  FaGraduationCap,
  FaHome,
  FaPiggyBank,
  FaPlane,
  FaReceipt,
  FaShoppingBag,
  FaTag,
  FaUtensils,
  FaWifi,
} from 'react-icons/fa';
import { MdLocalGroceryStore } from 'react-icons/md';

const CATEGORY_ICON_RULES = [
  { type: 'food', keywords: ['food', 'dining', 'restaurant', 'meal', 'lunch', 'dinner'] },
  { type: 'grocery', keywords: ['grocery', 'groceries', 'supermarket'] },
  { type: 'travel', keywords: ['travel', 'flight', 'trip', 'hotel'] },
  { type: 'transport', keywords: ['transport', 'bus', 'train', 'metro', 'taxi', 'cab', 'commute'] },
  { type: 'vehicle', keywords: ['fuel', 'car', 'vehicle', 'parking'] },
  { type: 'home', keywords: ['rent', 'home', 'house', 'mortgage'] },
  { type: 'shopping', keywords: ['shopping', 'clothes', 'apparel'] },
  { type: 'medical', keywords: ['medical', 'health', 'doctor', 'pharmacy'] },
  { type: 'education', keywords: ['education', 'course', 'book', 'tuition'] },
  { type: 'entertainment', keywords: ['entertainment', 'movie', 'game', 'games'] },
  { type: 'utilities', keywords: ['utilities', 'electricity', 'power', 'water'] },
  { type: 'internet', keywords: ['internet', 'wifi', 'phone', 'mobile'] },
  { type: 'saving', keywords: ['saving', 'investment', 'invest'] },
  { type: 'bill', keywords: ['bill', 'subscription', 'invoice'] },
];

function getCategoryIconType(category) {
  const normalizedCategory = category?.toLowerCase() || '';
  const match = CATEGORY_ICON_RULES.find(({ keywords }) =>
    keywords.some((keyword) => normalizedCategory.includes(keyword)),
  );

  return match?.type || 'default';
}

function CategoryIcon({ category, className = '' }) {
  const iconProps = {
    className,
    'aria-hidden': true,
    focusable: 'false',
  };

  switch (getCategoryIconType(category)) {
    case 'food':
      return <FaUtensils {...iconProps} />;
    case 'grocery':
      return <MdLocalGroceryStore {...iconProps} />;
    case 'travel':
      return <FaPlane {...iconProps} />;
    case 'transport':
      return <FaBus {...iconProps} />;
    case 'vehicle':
      return <FaCarSide {...iconProps} />;
    case 'home':
      return <FaHome {...iconProps} />;
    case 'shopping':
      return <FaShoppingBag {...iconProps} />;
    case 'medical':
      return <FaBriefcaseMedical {...iconProps} />;
    case 'education':
      return <FaGraduationCap {...iconProps} />;
    case 'entertainment':
      return <FaGamepad {...iconProps} />;
    case 'utilities':
      return <FaBolt {...iconProps} />;
    case 'internet':
      return <FaWifi {...iconProps} />;
    case 'saving':
      return <FaPiggyBank {...iconProps} />;
    case 'bill':
      return <FaReceipt {...iconProps} />;
    default:
      return <FaTag {...iconProps} />;
  }
}

export default CategoryIcon;
